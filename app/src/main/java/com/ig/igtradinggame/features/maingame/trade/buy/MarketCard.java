package com.ig.igtradinggame.features.maingame.trade.buy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ig.igtradinggame.R;
import com.ig.igtradinggame.models.CardModel;
import com.ig.igtradinggame.models.MarketModel;
import com.ig.igtradinggame.ui.BaseCardView;

import java.text.DecimalFormat;

import butterknife.BindView;

public class MarketCard extends BaseCardView {
    @BindView(R.id.textView_marketcard_id)
    TextView marketID;

    @BindView(R.id.textView_marketcard_name)
    TextView marketName;

    @BindView(R.id.textView_marketcard_price)
    TextView marketPrice;

    private MarketCard(View itemView) {
        super(itemView);
    }

    public static MarketCard newInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_market, parent, false);
        return new MarketCard(view);
    }

    @Override
    public void setup(CardModel cardView) {
        super.setup(cardView);

        if (cardView.getType() == MarketModel.TYPE) {
            MarketModel marketModel = (MarketModel) cardView;

            marketID.setText(marketModel.getMarketId());
            marketName.setText(marketModel.getMarketName());

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            marketPrice.setText(decimalFormat.format(marketModel.getCurrentPrice()));
        }
    }
}
