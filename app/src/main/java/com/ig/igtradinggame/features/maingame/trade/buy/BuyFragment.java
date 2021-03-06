package com.ig.igtradinggame.features.maingame.trade.buy;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ig.igtradinggame.R;
import com.ig.igtradinggame.features.maingame.trade.buy.BuyPopupView.PopupCallback;
import com.ig.igtradinggame.models.CardModel;
import com.ig.igtradinggame.models.MarketModel;
import com.ig.igtradinggame.models.OpenPositionIdResponse;
import com.ig.igtradinggame.network.retrofit_impl.IGAPIService;
import com.ig.igtradinggame.storage.AppStorage;
import com.ig.igtradinggame.ui.BaseCardView;
import com.ig.igtradinggame.ui.BaseFragment;
import com.ig.igtradinggame.ui.CardListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;

public class BuyFragment extends BaseFragment implements BaseCardView.OnItemClickListener {
    private static final int HEARTBEAT_FREQUENCY_MILLIS = 500;

    @BindView(R.id.recyclerView_buy)
    RecyclerView buyRecyclerView;

    private IGAPIService apiService;
    private ArrayList<CardModel> cardModelList;
    private CardListAdapter adapter;
    private boolean shouldUpdatePrices = true;

    public BuyFragment() {
        cardModelList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_position, container, false);
        unbinder = ButterKnife.bind(this, view);
        setup();
        return view;
    }

    @Override
    public void onDestroyView() {
        shouldUpdatePrices = false;
        super.onDestroyView();
    }

    private void setup() {
        this.apiService = new IGAPIService(AppStorage.getInstance(getActivity()).loadBaseUrl());

        setupRecyclerView();
        setupCards();
    }

    private void setupCards() {
        apiService.getAllMarketsStreaming(HEARTBEAT_FREQUENCY_MILLIS)
                .takeWhile(new Predicate<List<MarketModel>>() {
                    @Override
                    public boolean test(@NonNull List<MarketModel> marketModels) throws Exception {
                        return shouldUpdatePrices;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MarketModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull List<MarketModel> marketModels) {
                        cardModelList.clear();
                        cardModelList.addAll(marketModels);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setupRecyclerView() {
        buyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (buyRecyclerView != null) {
            adapter = new CardListAdapter(cardModelList, this);
            buyRecyclerView.setAdapter(adapter);
        }

        buyRecyclerView.setLayoutManager(linearLayoutManager);

        // Turn off blinking when updating
        RecyclerView.ItemAnimator animator = buyRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    @Override
    public void onItemClick(CardModel cardModel) {
        final BuyPopupView bottomsheet = new BuyPopupView();
        bottomsheet.addModel(cardModel);
        bottomsheet.show(getActivity().getSupportFragmentManager(), "buy_bottomsheet");

        bottomsheet.setPopupCallback(new PopupCallback() {
            @Override
            public void onSuccess(OpenPositionIdResponse response) {
                bottomsheet.dismiss();
                cardModelList = new ArrayList<>();
                adapter.notifyDataSetChanged();
                setup();

                showMessage("Purchased", false);
            }

            @Override
            public void onError(String errorMessage) {
                bottomsheet.dismiss();
                showMessage(errorMessage, true);
            }
        });
    }
}
