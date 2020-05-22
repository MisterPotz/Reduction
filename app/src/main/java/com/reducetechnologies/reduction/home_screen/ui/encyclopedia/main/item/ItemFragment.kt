package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.item//package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main.item
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import androidx.navigation.fragment.navArgs
//import com.reducetechnologies.command_infrastructure.MathTextSpec
//import com.reducetechnologies.command_infrastructure.PField
//import com.reducetechnologies.command_infrastructure.PFieldType
//import com.reducetechnologies.command_infrastructure.PScreen
//
//import com.reducetechnologies.reduction.R
//import com.reducetechnologies.reduction.android.util.PScreenSimpleeInflater
//
///**
// * A simple [Fragment] subclass.
// * Use the [ItemFragment.newInstance] factory method to
// * create an instance of this fragment.
// */
//class ItemFragment : Fragment() {
//    private lateinit var container : FrameLayout
//    private val args by navArgs<ItemFragmentArgs>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_item, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        container = view.findViewById(R.id.itemContainer)
//        showPScreen()
//    }
//
//    private fun showPScreen() {
//        val gson = PField.makeGson()
//        val pscreen = gson.fromJson(args.itemPScreen, PScreen::class.java)
////        pscreen.fields.filter { it.pFieldType == PFieldType.MATH_TEXT }.forEach {
////            (it.typeSpecificData as MathTextSpec).text
////            (it.typeSpecificData as MathTextSpec).text = addBackslashes((it.typeSpecificData as MathTextSpec).text)
////        }
//        PScreenSimpleeInflater.inflatPScreen(pscreen, container, resources.displayMetrics, activity!!.windowManager, layoutInflater, false)
//    }
//
//    private fun addBackslashes(string : String): String {
//        return string.split("\\").joinToString(separator = "\\\\")
//    }
//}
