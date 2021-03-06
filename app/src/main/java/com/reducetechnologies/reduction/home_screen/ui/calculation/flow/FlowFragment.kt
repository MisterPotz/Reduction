package com.reducetechnologies.reduction.home_screen.ui.calculation.flow

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import com.reducetechnologies.calculations_entity.ReducerData
import com.reducetechnologies.command_infrastructure.*
import com.reducetechnologies.reduction.R
import com.reducetechnologies.reduction.android.util.App
import com.reducetechnologies.reduction.android.util.ResultListContainer
import com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.*
import com.reduction_technologies.database.di.ApplicationScope
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.PrintWriter
import java.io.StringWriter
import javax.inject.Inject

/**
 * Responsible for building protoscreen, and taking argument from it
 */
class FlowFragment() : Fragment() {
    @Inject
    @ApplicationScope
    lateinit var viewModel: SharedViewModel

    private lateinit var controlPrev: Button
    private lateinit var controlNext: Button
    private lateinit var controlEnter: Button
    private lateinit var cardContainer: FrameLayout
    private lateinit var buttonStateDelegate: ButtonStateDelegate
    private lateinit var pScreenInflater: PScreenInflater

    private val handler by lazy {
        CoroutineExceptionHandler { context, exception ->
            val outError = StringWriter()

            exception.printStackTrace(PrintWriter(outError))
            val errorString: String = outError.toString()

            Timber.e("Error: $context, \n${errorString}")
        }
    }

    private fun CoroutineScope.launchSupervisor(
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return CoroutineScope(SupervisorJob(coroutineContext[Job])).launch(
            context = handler,
            block = block
        )
    }

    private fun setupEnterButton() {
        controlEnter.setOnClickListener {
            if (pScreenInflater.getFilled() != null) {
                Timber.i("Waiting for validation")
                lifecycleScope.launchSupervisor {
                    val curr = pScreenInflater.getFilled()
                    viewModel.enter(curr!!)
                    updateScreen()
                    fetchAllButtons()
                }
            } else {
                Toast.makeText(context, getString(R.string.enter_data), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupPrevButton() {
        controlPrev.setOnClickListener {
            lifecycleScope.launchSupervisor {
                viewModel.prev()
                updateScreen()
            }
        }
    }

    private fun setupNextButton() {
        controlNext.setOnClickListener {
            Timber.i("Waiting next")
            lifecycleScope.launchSupervisor {
                viewModel.next()
                updateScreen()
            }
        }
    }

    /**
     * By current contract, if pfield has non-encyclopedia links, it must be final to allow link
     */
    private suspend fun updateScreen() {
        val output = viewModel.requestOutput()
        when(output) {
            is Error -> {
                withContext(Dispatchers.Main) {
                    onCalculationError(output)
                }
            }
            is Screen -> {
                val links = if (viewModel.needsAttachingLinks()) getLinkCallbacks() else null
                withContext(Dispatchers.Main) {
                    pScreenInflater.showPScreen(output.pScreen, viewModel.isNecessaryInput(), links)
                    fetchAllButtons()
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun navigateToResult(single: Boolean, argument: Any) {
        val gson = GsonBuilder().create()
        val action =
            if (single) {
                val string = (argument as ReducerData).let { gson.toJson(it) }
                FlowFragmentDirections.actionFlowFragmentToResultsActivity(string)
            } else {
                val list = (argument as ArrayList<ReducerData>)
                val string = ResultListContainer(list).let { gson.toJson(it) }
                FlowFragmentDirections.actionFlowFragmentToResultList(string)
            }
        findNavController().navigate(action)
    }

    private fun getLinkCallbacks(): HashMap<Destination, LinkCalledCallback> {
        val results = viewModel.getCalculationResults()
        Timber.i("Got results")
        return hashMapOf(
            DestinationResult to {
                Timber.i("Must open best result")
                navigateToResult(true, results.simple[0])
            },
            DestinationSortedResultList(Sorted.WEIGHT) to {
                Timber.i("Opening list of weight sorted")
                navigateToResult(false, results.weight)
            },
            DestinationSortedResultList(Sorted.DIFF_SGD_SG) to {
                Timber.i("Opening diff sgd - sg sorted")
                navigateToResult(false, results.diffSGD)
            },
            DestinationSortedResultList(Sorted.VOLUME) to {
                Timber.i("Opening volume sorted")
                navigateToResult(false, results.volume)
            },
            DestinationSortedResultList(Sorted.SUM_AW) to {
                Timber.i("Opening sum aw sorted")
                navigateToResult(false, results.sumAw)
            },
            DestinationSortedResultList(Sorted.SUM_HRC) to {
                Timber.i("Opening sum hrc ")
                navigateToResult(false, results.hrcMin)
            },
            DestinationSortedResultList(Sorted.U_DESC) to {
                Timber.i("Opening u desc")
                navigateToResult(false, results.uDesc)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val main = inflater.inflate(R.layout.split_control_card_layout, container, false).apply {
            val frame = findViewById<FrameLayout>(R.id.controls)
            createControlView(inflater, frame)
//            frame.addView(controlModule)
        }
        cardContainer = main.findViewById(R.id.card)
        return main
    }

    private fun createControlView(inflater: LayoutInflater, parent: ViewGroup) {
        val controlModule = inflater.inflate(R.layout.calculation_flow_control, parent, true)
        controlPrev = controlModule.findViewById(R.id.controlPrev)
        controlNext = controlModule.findViewById(R.id.controlNext)
        controlEnter = controlModule.findViewById(R.id.controlEnter)
        buttonStateDelegate = ButtonStateDelegate(controlPrev, controlNext, controlEnter)
        setupEnterButton()
        setupNextButton()
        setupPrevButton()
    }

    private fun onCalculationError(calculationResults: Error) {
        if (calculationResults is CalculationNotPossible) {
            Toast.makeText(
                context,
                "Расчет не может быть проведен: некорректный ввод",
                Toast.LENGTH_LONG
            ).show()
        }
        findNavController().popBackStack()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_flow, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_finish -> {
                Timber.i("Finish called")
                viewModel.finishCurrentCalculation()
                onCalculationError(FinishedEarly)
                true
            }
            else -> false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity!!.application as App).appComponent.inject(this)
        // obtaining model delegate, responsible for switching screens
        pScreenInflater = PScreenInflater(
            context!!,
            cardContainer,
            activity!!.windowManager,
            resources.displayMetrics
        )
        lifecycleScope.launchSupervisor{
            updateScreen()
        }
    }

    private fun fetchPrevStatus() {
        buttonStateDelegate.hasPrev(viewModel.havePrev())
    }

    private fun fetchNeedsInput() {
        buttonStateDelegate.mustBeEntered(viewModel.isNecessaryInput())
    }

    private fun fetchHasNext() {

        buttonStateDelegate.hasNext(viewModel.haveNext())
    }

    private fun fetchAllButtons() {
        fetchHasNext()
        fetchNeedsInput()
        fetchPrevStatus()
    }
}