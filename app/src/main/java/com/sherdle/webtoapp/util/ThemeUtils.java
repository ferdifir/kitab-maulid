package com.sherdle.webtoapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.R;

public class ThemeUtils {

    /**
     * Provide all menuItems in a menu with a tint of the theme's toolbarForeground color
     * @param menu to apply tint to
     * @param context Context
     */
    public static void tintAllIcons(Menu menu, Context context) {
        //Retrieve toolbar foreground color
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.toolbarForeground, typedValue, true);
        int color = typedValue.data;

        tintAllIcons(menu, context, color);
    }

    private static void tintAllIcons(Menu menu, Context context, int color) {
        for (int i = 0; i < menu.size(); ++i) {
            final MenuItem item = menu.getItem(i);

            tintMenuItemIcon(color, item);
            //tintShareIconIfPresent(color, item);
        }
    }

    public static void setTheme(Activity activity){
        activity.setTheme(
                Config.LIGHT_TOOLBAR_THEME ? R.style.AppTheme_Light : R.style.AppTheme);
    }

    /**
     * @param context Context
     * @return Whether the toolbar background is light or coloured (i.e. black, blue)
     */
    public static boolean lightToolbarThemeActive(Context context){
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.toolbarBackground, value, true);
        return value.data == ContextCompat.getColor(context, R.color.white);
    }

    /**
     * Set the content of a toolbar to a certain color
     * @param toolbar Toolbar content to apply theme to.
     * @param color Color
     */
    public static void setToolbarContentColor(Toolbar toolbar, int color){
        //Tint elements
        applyTintToDrawable(toolbar.getOverflowIcon(), color);
        applyTintToDrawable(toolbar.getNavigationIcon(), color);
        toolbar.setTitleTextColor(color);

        //Tint menu items
        tintAllIcons(toolbar.getMenu(), toolbar.getContext(), color);
    }

    private static void applyTintToDrawable(Drawable drawable, int color) {
        if (drawable == null)
            return;
        final Drawable wrapped = DrawableCompat.wrap(drawable);
        drawable.mutate();
        DrawableCompat.setTint(wrapped, color);
    }

    private static void tintMenuItemIcon(int color, MenuItem item) {
        final Drawable drawable = item.getIcon();
        if (drawable != null) {
            final Drawable wrapped = DrawableCompat.wrap(drawable);
            drawable.mutate();
            DrawableCompat.setTint(wrapped, color);
            item.setIcon(drawable);
        }
    }

    /**
     * Unused
     */


}
