package com.ig.igtradinggame.ui.fragments;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ig.igtradinggame.R;
import com.ig.igtradinggame.models.OpenPositionModel;
import com.ig.igtradinggame.network.IGAPIService;
import com.ig.igtradinggame.storage.ClientIDStorage;
import com.ig.igtradinggame.storage.SharedPreferencesStorage;
import com.ig.igtradinggame.ui.cards.BaseCardView;
import com.ig.igtradinggame.ui.cards.CardModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;


public class CurrentPositionsFragment extends BaseFragment implements BaseCardView.OnItemClickListener {
    public static final int HEARTBEAT_FREQUENCY_MILLIS = 500;

    @BindView(R.id.recyclerView_current_positions)
    RecyclerView currentPositionsRecyclerView;

    private IGAPIService apiService;
    private OpenPositionListAdapter adapter;
    private ArrayList<CardModel> cardModelList;
    private boolean shouldUpdatePrices = true;

    public CurrentPositionsFragment() {
        this.cardModelList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_positions, container, false);
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
        this.apiService = new IGAPIService();
        setupRecyclerView();
        setupCards();
    }

    private void setupCards() {
        ClientIDStorage storage = new SharedPreferencesStorage(PreferenceManager.getDefaultSharedPreferences(getActivity()));

        currentPositionsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                Log.e(TAG, "onTouchEvent: INTERCEPT TOUCH EVENT");
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        apiService.getOpenPositionsStreaming(storage.loadClientId(), HEARTBEAT_FREQUENCY_MILLIS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<OpenPositionModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<OpenPositionModel> openPositionModels) {
                        cardModelList.clear();
                        cardModelList.addAll(openPositionModels);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void setupRecyclerView() {
        currentPositionsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (currentPositionsRecyclerView != null) {
            adapter = new OpenPositionListAdapter(cardModelList, this);
            currentPositionsRecyclerView.setAdapter(adapter);
        }

        currentPositionsRecyclerView.setLayoutManager(linearLayoutManager);

        // Turn off blinking when updating
        RecyclerView.ItemAnimator animator = currentPositionsRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    @Override
    public void onItemClick(CardModel cardModel) {
        Log.e(TAG, "onItemClick: CARD CLICKED!");
    }
}