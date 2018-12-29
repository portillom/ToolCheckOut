package com.michaelportillo.android.toolcheckout;

import android.support.v4.app.Fragment;

public class ToolListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new ToolListFragment();
    }

}
