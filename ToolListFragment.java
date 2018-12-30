package com.michaelportillo.android.toolcheckout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelportillo.android.toolcheckout.database.ToolBaseHelper;

import java.util.List;

/**
 * Created by USER on 12/4/18.
 */

public class ToolListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mToolRecyclerView;
    private ToolAdapter mAdapter;
    private boolean mSubtitleVisible;

    /**
     * The FragmentManager is responsible for calling Fragment.onCreateOptionsMenu(Menu, MenuInflater)
     * when the activity receives its onCreateOptionsMenu(..) callback from the OS. FragmentManager
     * must be explicitly told that the fragment should receive a call to onCreateOptionsMenu(..)
     * This is done by calling public void setHasOptionsMenu(boolean hasMenu)       M.3
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Don't forget the LayoutManager. Without it, the app will crash. RecyclerView does not position
     * items on the screen itself. It delegates that job to the LayoutManager. This positions every
     * item and also defines how scrolling works.       R.1 (R.0.1 Creating fragment_tool_list.xml)
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_tool_list, container, false);
        mToolRecyclerView = (RecyclerView) view.findViewById(R.id.tool_recycler_view);
        mToolRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);       //M.11
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    /**
     * This method solves the rotation issue by saving mSubtitleVisible instance variable across
     * rotation with the saved instance state mechanism.        //M.12
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    /**
     * Within this method, MenuInflater.inflate(int, Menu) is called and passes in the resource ID
     * of the menu file. This populates the Menu instance with the items defined in your file.
     *
     * Calling the superlcalss implementation on onCreateOptionsMenu(..) is not required and only
     * done for conventional purposes.          M.2 (M.1 menu/xml)
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_tool_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);      //M.7
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    /**
     * This responds to selection of the MenuItem by creating a new Tool, adding it to the ToolBox,
     * and then starting an instance of ToolPagerActivity to edit the new Tool.     M.4
     *
     * @param item
     * @return true to indicate that no further processing is necessary. The default case calls the
     * superclass implementation if the item ID is not in the implementation.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_tool:
                Tool tool = new Tool();
                ToolBox.get(getActivity()).addTool(tool);
                Intent intent = ToolPagerActivity.newIntent(getActivity(), tool.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;       //M.8
                getActivity().invalidateOptionsMenu();      //M.8
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method will display the number of tools on the menu as a subtitle
     * This method first generates the subtitle string using the getString(int resId, Object.. formatArgs)
     * method, which accepts replacement values for the placeholders in the string resource.
     *
     * Next, the activity that is hosting the ToolListFragment is cast to an AppCompatActivity.
     * Recall that because this app uses the AppCompat library, all activities are a subclass of
     * AppCompatActivity, which allows you to access the toolbar.
     *
     *          M.5(menu/xml add item) M.6
     */
    private void updateSubtitle(){
        int toolCount = ToolBox.get(getActivity()).getTools().size();
        String subtitle = getString(R.string.subtitle_format, toolCount);

        if (!mSubtitleVisible){         //M.9
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    /**
     *      R.5
     */
    private void updateUI(){
        ToolBox toolBox = ToolBox.get(getActivity());
        List<Tool> tools = toolBox.getTools();

        if(mAdapter == null){
            mAdapter = new ToolAdapter(tools);
            mToolRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.setTools(tools);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();           //M.10

    }

    /**
     * In ToolHolder's constructor, list_item_tool.xml is inflated and immediately passed into super,
     * ViewHolder's constructor. The base ViewHolder class will then hold on to the fragment_tool_list.xml
     * view hierarchy.          R.2
     */
    private class ToolHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mToolNameTextView;
        private TextView mDateTextView;
        private Tool mTool;
        private ImageView mReturnedImageView;
        private TextView mReturnDateTextView;

        public ToolHolder(LayoutInflater inflater, ViewGroup parent, int viewType){
            super(inflater.inflate(viewType, parent, false));
            itemView.setOnClickListener(this);

            mToolNameTextView = (TextView) itemView.findViewById(R.id.tool_name);       //R.6
            mDateTextView = (TextView) itemView.findViewById(R.id.tool_date);           //R.6
            mReturnDateTextView = (TextView) itemView.findViewById(R.id.tool_return_date);
            mReturnedImageView = (ImageView) itemView.findViewById(R.id.tool_returned_image);
        }

        /**
         *This method will be called each time a new Tool should be displayed in ToolHolder.
         * ToolHolder will now update the title TextView and date TextView to reflect the
         * state of the Tool.
         *                      R.7
         * @param tool
         */
        public void bind(Tool tool){
            mTool = tool;
            mToolNameTextView.setText(mTool.getToolName());
            mDateTextView.setText(String.format(getString(R.string.list_item_out)) + DateFormat.format("MM/dd/yy h:mm a", mTool.getDate()).toString());
            if(tool.isReturned()){
                mReturnDateTextView.setText(String.format(getString(R.string.list_item_in))
                        + DateFormat.format("MM/dd/yy h:mm a", mTool.getReturnDate())
                        .toString());
            }
            mReturnedImageView.setVisibility(tool.isReturned() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view){
            Intent intent = ToolPagerActivity.newIntent(getActivity(), mTool.getId());
            startActivity(intent);
        }
    }


    /**
     * When the RecyclerView needs to display a new ViewHolder or connect a Tool object to an
     * existing ViewHolder, it will ask this adapter for help by calling a method on it. The RecyclerView
     * itself will not know anything about the Tool object, but the adapter will know all of Tool's
     * intimate personal details.           R.3
     */
    private class ToolAdapter extends RecyclerView.Adapter<ToolHolder>{
        private List<Tool> mTools;

        public ToolAdapter(List<Tool> tools){
            mTools = tools;
        }

        /**
         * This method is called by the RecyclerView when it needs a new ViewHolder to display an
         * item with. In this method, a LayoutInflater was created and used to construct a new ToolHolder.
         *                      R.4
         * @param parent
         * @param viewType
         * @return ToolHolder
         */
        @Override
        public ToolHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ToolHolder(layoutInflater, parent, viewType);

        }

        /**
         *The bind(Tool) method will now be called each time the RecyclerView requests that a given
         * ToolHolder be bound to a particular tool.
         *  R.8
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(ToolHolder holder, int position) {
            Tool tool = mTools.get(position);
            holder.bind(tool);

        }

        @Override
        public int getItemViewType(int position){
            if(mTools.get(position).isReturned()){
                return R.layout.list_item_tool_return_date;
            }else{
                return R.layout.list_item_tool;
            }
        }

        @Override
        public int getItemCount() {
            return mTools.size();
        }

        public void setTools(List<Tool> tools){
            mTools = tools;
        }


    }



}
