package com.example.travelappui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;

import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.wwdablu.soumya.lottiebottomnav.FontBuilder;
import com.wwdablu.soumya.lottiebottomnav.FontItem;
import com.wwdablu.soumya.lottiebottomnav.ILottieBottomNavCallback;
import com.wwdablu.soumya.lottiebottomnav.LottieBottomNav;
import com.wwdablu.soumya.lottiebottomnav.MenuItem;
import com.wwdablu.soumya.lottiebottomnav.MenuItemBuilder;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ILottieBottomNavCallback/*, MailFragment.ClickHandler*/{

    private MaterialToolbar mToolBar;

    private FloatingNavigationView mFloatingNavigationView;


    private LottieBottomNav bottomNav;
    private ArrayList<MenuItem> list;
    private ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Custom Action Bar
        mToolBar = (MaterialToolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        float radius = getResources().getDimension(com.shashank.sony.fancywalkthroughlib.R.dimen.activity_half_margin);
        MaterialShapeDrawable toolbarBackground = (MaterialShapeDrawable) mToolBar.getBackground();
        toolbarBackground.setShapeAppearanceModel(
                toolbarBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setBottomRightCorner(CornerFamily.ROUNDED,radius)
                        .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
                        .build()
        );
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_app_bar, null);
        actionBar.setCustomView(actionBarView);

        // Floating Navigation View
        mFloatingNavigationView = (FloatingNavigationView) findViewById(R.id.floating_navigation_view);
        mFloatingNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingNavigationView.open();
            }
        });
        mFloatingNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(android.view.MenuItem item) {
                Snackbar.make((View) mFloatingNavigationView.getParent(), item.getTitle() + " Selected!", Snackbar.LENGTH_SHORT).show();
                mFloatingNavigationView.close();
                return true;
            }
        });


        // LottieBottomNav
        bottomNav = findViewById(R.id.bottom_nav);

        mFragments = new ArrayList<>(4);

        mFragments.add(new HomeFragment());
        mFragments.add(new GiftFragment());

        MailFragment mailFragment = new MailFragment();
        //mailFragment.setClickHandler(this);
        mFragments.add(mailFragment);

        mFragments.add(new SettingsFragment());

        FontItem fontItem = FontBuilder.create("Dashboard")
                .selectedTextColor(Color.WHITE)
                .unSelectedTextColor(Color.GRAY)
                .selectedTextSize(16) //SP
                .unSelectedTextSize(12) //SP
                .setTypeface(Typeface.createFromAsset(getAssets(), "coffeesugar.ttf"))
                .build();

        MenuItem item1 = MenuItemBuilder.create("home.json", MenuItem.Source.Assets, fontItem, "dash")
                .pausedProgress(1f)
                .loop(false)
                .build();

        SpannableString spannableString = new SpannableString("Gifts");
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        fontItem = FontBuilder.create(fontItem).setTitle(spannableString).build();
        MenuItem item2 = MenuItemBuilder.createFrom(item1, fontItem)
                .selectedLottieName("gift.json")
                .unSelectedLottieName("gift.json")
                .loop(true)
                .build();

        fontItem = FontBuilder.create(fontItem).setTitle("Mail").build();
        MenuItem item3 = MenuItemBuilder.createFrom(item1, fontItem)
                .selectedLottieName("message_cupid.json")
                .unSelectedLottieName("message.json")
                .pausedProgress(0.75f)
                .build();

        fontItem = FontBuilder.create(fontItem).setTitle("Settings").build();
        MenuItem item4 = MenuItemBuilder.createFrom(item1, fontItem)
                .selectedLottieName("settings.json")
                .unSelectedLottieName("settings.json")
                .build();

        list = new ArrayList<>(4);
        list.add(item1);
        list.add(item2);
        list.add(item3);
        list.add(item4);

        bottomNav.setCallback(this);
        bottomNav.setMenuItemList(list);
        bottomNav.setSelectedIndex(0);

    }

    // Floating Navigation View
    @Override
    public void onBackPressed() {
        if (mFloatingNavigationView.isOpened()) {
            mFloatingNavigationView.close();
        } else {
            super.onBackPressed();
        }
    }




    // LottieBottomNav
    @Override
    public void onMenuSelected(int oldIndex, int newIndex, MenuItem menuItem) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_frag_container, mFragments.get(newIndex), mFragments.get(newIndex).getClass().getSimpleName())
                .commitAllowingStateLoss();
    }

    @Override
    public void onAnimationStart(int index, MenuItem menuItem) {
        //
    }

    @Override
    public void onAnimationEnd(int index, MenuItem menuItem) {

        if(index == 2 && menuItem.getTag() != null && "cupid".equalsIgnoreCase(menuItem.getTag().toString())) {
            restoreMenuForMessage(menuItem);
        }
    }

    @Override
    public void onAnimationCancel(int index, MenuItem menuItem) {
        if(index == 2 && menuItem.getTag() != null && "cupid".equalsIgnoreCase(menuItem.getTag().toString())) {
            restoreMenuForMessage(menuItem);
        }
    }

    private void restoreMenuForMessage(MenuItem menuItem) {
        MenuItem item = MenuItemBuilder.createFrom(menuItem)
                .selectedLottieName("message.json")
                .build();

        bottomNav.updateMenuItemFor(2, item);
    }
/*
    @Override
    public void onChangeMenuIcon() {

        if(bottomNav.getSelectedIndex() == 2) {

            com.wwdablu.soumya.lottiebottomnav.MenuItem cupidMessage = MenuItemBuilder.createFrom(bottomNav.getMenuItemFor(2))
                    .selectedLottieName("message_cupid.json")
                    .tag("cupid")
                    .build();

            bottomNav.updateMenuItemFor(2, cupidMessage);
        }
    }*/

}