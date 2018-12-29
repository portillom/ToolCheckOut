package com.michaelportillo.android.toolcheckout;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by USER on 12/7/18.
 */

public class ToolPagerActivity extends AppCompatActivity {

    private static final String EXTRA_TOOL_ID = "com.michaelportillo.android.toolcheckout.tool_id"; //vp.3

    private ViewPager mViewPager;
    private List<Tool> mTools;

    /**
     * Integrating ToolPagerActivity        vp.3
     * @param packageContext
     * @param toolId
     * @return
     */
    public static Intent newIntent(Context packageContext, UUID toolId){
        Intent intent = new Intent(packageContext, ToolPagerActivity.class);
        intent.putExtra(EXTRA_TOOL_ID, toolId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_pager);

        UUID toolId = (UUID) getIntent().getSerializableExtra(EXTRA_TOOL_ID);   //vp.4

        mViewPager = (ViewPager) findViewById(R.id.tool_view_pager);

        mTools = ToolBox.get(this).getTools();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            /**
             * This method fetches the Tool instance for the given position in the data set. It then
             * uses that Tool's ID to create and return a properly configured ToolFragment.
             *
             * @param position
             * @return
             */
            @Override
            public Fragment getItem(int position) {
                Tool tool = mTools.get(position);
                return ToolFragment.newInstance(tool.getId());
            }

            @Override
            public int getCount() {
                return mTools.size();
            }
        });

        /**
         * By default, the ViewPager shows the first item in its PagerAdapter. This loop sets the
         * list_item_tool that is selected by setting the ViewPager's current item to the index of
         * the selected tool.
         */
        for(int i = 0; i < mTools.size(); i++){
            if(mTools.get(i).getId().equals(toolId)){       //vp.5
                mViewPager.setCurrentItem(i);
                break;
            }

        }
    }
}
