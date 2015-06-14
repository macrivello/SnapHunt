package com.michaelcrivello.apps.snaphunt.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.michaelcrivello.apps.snaphunt.R;
import com.michaelcrivello.apps.snaphunt.data.model.Round;

import roboguice.util.Ln;

/**
 * tile view for game_activity screen that shows current round info
 */
public class RoundInfoTileView extends RelativeLayout {
    int roundNumber;
    TextView roundNumberText, themeWordText, timeRemainingText;
    String theme;
    Round round;

    public RoundInfoTileView(Context context, Round round) {
        super(context);
        this.round = round;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.round_info_tile, this, true);

        roundNumberText = (TextView) findViewById(R.id.roundNumberText);
        themeWordText = (TextView) findViewById(R.id.roundThemeWordText);
        timeRemainingText = (TextView) findViewById(R.id.roundTimeRemainingText);

        setRoundInfo();
    }

    private void setRoundInfo() {
        if (round != null) {
            roundNumberText.setText("Round " + round.getRoundNumber());
            themeWordText.setText(round.getSelectedTheme().getPhrase());
            timeRemainingText.setText("time remaining");
        } else {
            Ln.e("Attemping to set round info with NULL round object");
        }
    }

    public RoundInfoTileView(Context context) {
        this(context, null);
    }

    public void setRound(Round round) {
        this.round = round;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
