package kz.nis.findtheword;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;

/**
 * Created by myrza on 12/9/15.
 */
public class Card extends Button {

    public Card(Context context) {
        super(context);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            this.setTextColor(Color.WHITE);
        } else {
            this.setTextColor(Color.GRAY);
        }
    }
}
