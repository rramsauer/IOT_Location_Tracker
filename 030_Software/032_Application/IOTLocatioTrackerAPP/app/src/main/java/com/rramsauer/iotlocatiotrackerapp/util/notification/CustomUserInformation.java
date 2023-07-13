package com.rramsauer.iotlocatiotrackerapp.util.notification;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;
import com.rramsauer.iotlocatiotrackerapp.R;

/**
 * A class that provides static methods to show custom Snackbar messages with different colors and styles.
 *
 * @author Ramsauer René
 * @version 2.0
 */
public class CustomUserInformation {

    /**
     * Displays an error Snackbar message with a red background and white text.
     *
     * @param view        The view to attach the Snackbar to.
     * @param text        The text to display in the Snackbar.
     * @param duration    The duration of the Snackbar message. Use Snackbar.LENGTH_SHORT or Snackbar.LENGTH_LONG.
     * @param gravity     The gravity of the Snackbar message.
     * @param isRemovable A boolean value indicating whether the Snackbar is removable.
     * @author Ramsauer René
     */
    public static void showSnackbarError(@NonNull android.view.View view, @NonNull CharSequence text, @IntRange(from = -2) int duration, int gravity, boolean isRemovable) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        View view1 = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view1.getLayoutParams();
        params.gravity = gravity;
        params.setMargins(0, 0, 0, 250);
        view1.setLayoutParams(params);
        snackbar.setTextColor(Color.rgb(255, 255, 255))
                .setBackgroundTint(Color.rgb(195, 63, 56))
                .setActionTextColor(Color.rgb(255, 255, 255));
        if (isRemovable) {
            snackbar.setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        }
        snackbar.show();
    }

    /**
     * Displays a warning Snackbar message with a yellow background and black text.
     *
     * @param view        The view to attach the Snackbar to.
     * @param text        The text to display in the Snackbar.
     * @param duration    The duration of the Snackbar message. Use Snackbar.LENGTH_SHORT or Snackbar.LENGTH_LONG.
     * @param gravity     The gravity of the Snackbar message.
     * @param isRemovable A boolean value indicating whether the Snackbar is removable.
     * @author Ramsauer René
     */
    public static void showSnackbarWarning(@NonNull android.view.View view, @NonNull CharSequence text, @IntRange(from = -2) int duration, int gravity, boolean isRemovable) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        View view1 = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view1.getLayoutParams();
        params.gravity = gravity;
        params.setMargins(0, 0, 0, 250);
        view1.setLayoutParams(params);
        snackbar.setTextColor(Color.rgb(80, 80, 80))
                .setBackgroundTint(Color.rgb(255, 237, 70))
                .setActionTextColor(Color.rgb(80, 80, 80));
        if (isRemovable) {
            snackbar.setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        }
        snackbar.show();
    }

    /**
     * Displays a success Snackbar message with a green background and white text.
     *
     * @param view        The view to attach the Snackbar to.
     * @param text        The text to display in the Snackbar.
     * @param duration    The duration of the Snackbar message. Use Snackbar.LENGTH_SHORT or Snackbar.LENGTH_LONG.
     * @param gravity     The gravity of the Snackbar message.
     * @param isRemovable A boolean value indicating whether the Snackbar is removable.
     * @author Ramsauer René
     */
    public static void showSnackbarSucess(@NonNull android.view.View view, @NonNull CharSequence text, @IntRange(from = -2) int duration, int gravity, boolean isRemovable) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        View view1 = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view1.getLayoutParams();
        params.gravity = gravity;
        params.setMargins(0, 0, 0, 250);
        view1.setLayoutParams(params);
        snackbar.setTextColor(Color.rgb(255, 255, 255))
                .setBackgroundTint(Color.rgb(87, 178, 90))
                .setActionTextColor(Color.rgb(255, 255, 255));
        if (isRemovable) {
            snackbar.setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        }
        snackbar.show();
    }

    /**
     * Displays an informational Snackbar message with a blue background and white text.
     *
     * @param view        The view to attach the Snackbar to.
     * @param text        The text to display in the Snackbar.
     * @param duration    The duration of the Snackbar message. Use Snackbar.LENGTH_SHORT or Snackbar.LENGTH_LONG.
     * @param gravity     The gravity of the Snackbar message.
     * @param isRemovable A boolean value indicating whether the Snackbar is removable.
     * @author Ramsauer René
     */
    public static void showSnackbarInfo(@NonNull android.view.View view, @NonNull CharSequence text, @IntRange(from = -2) int duration, int gravity, boolean isRemovable) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        View view1 = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view1.getLayoutParams();
        params.gravity = gravity;
        params.setMargins(0, 0, 0, 250);
        view1.setLayoutParams(params);
        snackbar.setTextColor(Color.rgb(255, 255, 255))
                .setBackgroundTint(Color.rgb(10, 92, 202))
                .setActionTextColor(Color.rgb(255, 255, 255));
        if (isRemovable) {
            snackbar.setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        }
        snackbar.show();
    }

    /**
     * Displays a normal Snackbar message with a dark gray background and white text.
     *
     * @param view        The view to attach the Snackbar to.
     * @param text        The text to display in the Snackbar.
     * @param duration    The duration of the Snackbar message. Use Snackbar.LENGTH_SHORT or Snackbar.LENGTH_LONG.
     * @param gravity     The gravity of the Snackbar message.
     * @param isRemovable A boolean value indicating whether the Snackbar is removable.
     * @author Ramsauer René
     */
    public static void showSnackbarNormal(@NonNull android.view.View view, @NonNull CharSequence text, @IntRange(from = -2) int duration, int gravity, boolean isRemovable) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        View view1 = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view1.getLayoutParams();
        params.gravity = gravity;
        params.setMargins(0, 0, 0, 250);
        view1.setLayoutParams(params);
        snackbar.setTextColor(Color.rgb(255, 255, 255))
                .setBackgroundTint(Color.rgb(49, 49, 49))
                .setActionTextColor(Color.rgb(255, 255, 255));
        if (isRemovable) {
            snackbar.setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
        }
        snackbar.show();
    }

    /**
     * Shows a custom Snackbar with the given title and message on the specified view with the
     * application icon for the given duration and gravity.
     *
     * @param fragmentActivity The FragmentActivity from which the Snackbar is displayed.
     * @param view             The view on which the Snackbar is displayed.
     * @param titleText        The text to be displayed in the title of the Snackbar.
     * @param msgText          The text to be displayed in the message of the Snackbar.
     * @param duration         The duration for which the Snackbar is displayed, in milliseconds.
     * @param gravity          The gravity of the Snackbar.
     */
    public static void showSnackbarApplication(FragmentActivity fragmentActivity, @NonNull android.view.View view, String titleText, String msgText, @IntRange(from = -2) int duration, int gravity) {
        final Snackbar snackbar = Snackbar.make(view, "", duration);
        View custom = fragmentActivity.getLayoutInflater().inflate(R.layout.snackbar_custom, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        TextView snackBarMsgText = custom.findViewById(R.id.msg_snackbar);
        snackBarMsgText.setText(msgText);
        TextView snackBarTitleText = custom.findViewById(R.id.title_snackbar);
        snackBarTitleText.setText(titleText);
        Button snackBarCancelButton = custom.findViewById(R.id.button_snackbar);
        snackBarCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbarLayout.addView(custom, 0);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) custom.getLayoutParams();
        params.gravity = gravity;
        params.setMargins(0, 0, 0, 250);
        custom.setLayoutParams(params);
        snackbar.show();
    }

}
