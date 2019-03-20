package app.jspyn.jspyniot;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gigamole.navigationtabstrip.NavigationTabStrip;


public class IfLet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if_let);

        final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.nTS);
        navigationTabStrip.setTitles("My ifLet", "Create new ifLet");
        navigationTabStrip.setTabIndex(0, true);
        navigationTabStrip.setTitleSize(50);
        navigationTabStrip.setStripColor(getResources().getColor(R.color.accent,getTheme()));
        navigationTabStrip.setStripWeight(6);
        navigationTabStrip.setStripFactor(25);
        navigationTabStrip.setStripType(NavigationTabStrip.StripType.LINE);
        navigationTabStrip.setStripGravity(NavigationTabStrip.StripGravity.BOTTOM);
        navigationTabStrip.setTypeface("fonts/typeface.ttf");
        navigationTabStrip.setCornersRadius(3);
        navigationTabStrip.setAnimationDuration(300);
        navigationTabStrip.setInactiveColor(Color.GRAY);
        navigationTabStrip.setActiveColor(Color.WHITE);
        navigationTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        navigationTabStrip.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {

            }

            @Override
            public void onEndTabSelected(String title, int index) {

            }
        });

    }
}
