package com.ig.igtradinggame.features.maingame.about;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ig.igtradinggame.R;
import com.ig.igtradinggame.models.BalanceModel;
import com.ig.igtradinggame.ui.BaseCardView;
import com.ig.igtradinggame.models.CardModel;

import butterknife.BindView;


public final class BalanceCard extends BaseCardView {
    @BindView(R.id.textView_balance)
    TextView balanceText;

    private BalanceCard(View itemView) {
        super(itemView);
    }

    public static BalanceCard newInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_balance, parent, false);
        return new BalanceCard(view);
    }

    @Override
    public void setup(CardModel cardModel) {
        super.setup(cardModel);

        if (cardModel instanceof BalanceModel) {
            BalanceModel viewModel = (BalanceModel) cardModel;
            Double balance = viewModel.getBalance();
            balanceText.setText(balance.toString());
        }
    }
}