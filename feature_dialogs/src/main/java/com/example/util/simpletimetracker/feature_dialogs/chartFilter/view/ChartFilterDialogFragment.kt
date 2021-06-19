package com.example.util.simpletimetracker.feature_dialogs.chartFilter.view

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.util.simpletimetracker.core.adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.core.adapter.category.createCategoryAdapterDelegate
import com.example.util.simpletimetracker.core.adapter.empty.createEmptyAdapterDelegate
import com.example.util.simpletimetracker.core.adapter.loader.createLoaderAdapterDelegate
import com.example.util.simpletimetracker.core.adapter.recordType.createRecordTypeAdapterDelegate
import com.example.util.simpletimetracker.core.base.BaseBottomSheetDialogFragment
import com.example.util.simpletimetracker.core.di.BaseViewModelFactory
import com.example.util.simpletimetracker.core.dialog.ChartFilterDialogListener
import com.example.util.simpletimetracker.core.extension.blockContentScroll
import com.example.util.simpletimetracker.core.extension.getAllFragments
import com.example.util.simpletimetracker.core.extension.setOnClick
import com.example.util.simpletimetracker.core.extension.setSkipCollapsed
import com.example.util.simpletimetracker.feature_dialogs.R
import com.example.util.simpletimetracker.feature_dialogs.chartFilter.viewModel.ChartFilterViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.chart_filter_dialog_fragment.btnChartFilterHideAll
import kotlinx.android.synthetic.main.chart_filter_dialog_fragment.btnChartFilterShowAll
import kotlinx.android.synthetic.main.chart_filter_dialog_fragment.buttonsChartFilterType
import kotlinx.android.synthetic.main.chart_filter_dialog_fragment.rvChartFilterContainer
import javax.inject.Inject

@AndroidEntryPoint
class ChartFilterDialogFragment : BaseBottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<ChartFilterViewModel>

    private val viewModel: ChartFilterViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private val recordTypesAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createRecordTypeAdapterDelegate(viewModel::onRecordTypeClick),
            createCategoryAdapterDelegate(viewModel::onCategoryClick),
            createLoaderAdapterDelegate(),
            createEmptyAdapterDelegate()
        )
    }

    private var chartFilterDialogListener: ChartFilterDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.chart_filter_dialog_fragment,
            container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initDialog()
        initUi()
        initUx()
        initViewModel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is ChartFilterDialogListener -> {
                chartFilterDialogListener = context
                return
            }
            is AppCompatActivity -> {
                context.getAllFragments()
                    .firstOrNull { it is ChartFilterDialogListener && it.isResumed }
                    ?.let { chartFilterDialogListener = it as? ChartFilterDialogListener }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        chartFilterDialogListener?.onChartFilterDialogDismissed()
    }

    private fun initDialog() {
        setSkipCollapsed()
        blockContentScroll(rvChartFilterContainer)
    }

    private fun initUi() {
        rvChartFilterContainer.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
            }
            adapter = recordTypesAdapter
        }
    }

    private fun initUx() {
        buttonsChartFilterType.listener = viewModel::onFilterTypeClick
        btnChartFilterShowAll.setOnClick(viewModel::onShowAllClick)
        btnChartFilterHideAll.setOnClick(viewModel::onHideAllClick)
    }

    private fun initViewModel(): Unit = with(viewModel) {
        filterTypeViewData.observe(buttonsChartFilterType.adapter::replace)
        types.observe(recordTypesAdapter::replace)
    }
}