package com.duyp.architecture.mvvm.ui.base.fragment;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.duyp.architecture.mvvm.Constants;
import com.duyp.architecture.mvvm.R;
import com.duyp.architecture.mvvm.ui.base.BaseListDataViewModel;
import com.duyp.architecture.mvvm.ui.base.OnLoadMore;
import com.duyp.architecture.mvvm.ui.base.adapter.BaseRecyclerViewAdapter;
import com.duyp.architecture.mvvm.ui.base.interfaces.UiRefreshable;
import com.duyp.architecture.mvvm.ui.widgets.StateLayout;
import com.duyp.architecture.mvvm.ui.widgets.recyclerview.DynamicRecyclerView;
import com.duyp.architecture.mvvm.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import javax.inject.Inject;

import io.realm.RealmObject;

/**
 * Created by duypham on 10/23/17.
 *
 */

public abstract class BaseRecyclerViewFragment<
        B extends ViewDataBinding,
        T extends RealmObject,
        A extends BaseRecyclerViewAdapter<T>,
        VM extends BaseListDataViewModel<T, A>>
        extends BaseViewModelFragment<B, VM> implements UiRefreshable{

    protected SwipeRefreshLayout refreshLayout;
    protected DynamicRecyclerView recyclerView;
    protected StateLayout stateLayout;
    protected RecyclerViewFastScroller fastScroller;
    private boolean isRefreshing;

    @Inject
    A adapter;

    @Inject
    OnLoadMore onLoadMore;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        stateLayout = view.findViewById(R.id.stateLayout);
        fastScroller = view.findViewById(R.id.fastScroller);

        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(stateLayout, refreshLayout);
        stateLayout.setOnReloadListener(v -> refreshWithUi());

        fastScroller.attachRecyclerView(recyclerView);

        refreshLayout.setColorSchemeResources(R.color.material_amber_700, R.color.material_blue_700,
                R.color.material_purple_700, R.color.material_lime_700);
        refreshLayout.setOnRefreshListener(this::refresh);
        viewModel.initAdapter(adapter);
    }

    @Override
    protected int getLayout() {
        return R.layout.refresh_recycler_view;
    }

    @Override
    public void setLoading(boolean loading) {
        if (!loading) {
            doneRefresh();
        } else {
            refreshUi();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        onLoadMore.init(recyclerView, viewModel);
    }

    @Override
    public void onStop() {
        super.onStop();
        onLoadMore.unRegisterListener(recyclerView);
    }

    @Override
    public void refresh() {
        if (!isRefreshing) {
            viewModel.refresh();
            onLoadMore.reset();
            isRefreshing = true;
        }
    }

    @Override
    public void doneRefresh() {
        if (refreshLayout != null) {
            refreshLayout.post(() -> refreshLayout.setRefreshing(false));
        }
        isRefreshing = false;
    }

    @Override
    public void refreshWithUi() {
        refreshWithUi(0);
    }

    @Override
    public void refreshWithUi(int delay) {
        if (refreshLayout != null) {
            refreshLayout.postDelayed(() -> {
                refreshUi();
                refresh();
            }, delay);
        }
    }

    private boolean shouldRefreshUi = true;
    protected void refreshUi() {
        shouldRefreshUi = true;
        new android.os.Handler().postDelayed(() -> {
            if (shouldRefreshUi && refreshLayout != null) {
                refreshLayout.setRefreshing(true);
            }
        }, Constants.PROGRESS_DIALOG_DELAY);
    }

    @Override
    public void setRefreshEnabled(boolean enabled) {
        refreshLayout.setEnabled(enabled);
    }
}