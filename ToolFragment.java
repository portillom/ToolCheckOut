package com.michaelportillo.android.toolcheckout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by USER on 12/5/18.
 */

public class ToolFragment extends Fragment {

    private static final String ARG_TOOL_ID = "tool_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_RETURN_DATE = "DialogRetrunDate";
    private static final String DIALOG_RETURN_TIME = "DialogReturnTime";

    private static final int REQUST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_RETURN_DATE = 4;
    private static final int REQUEST_RETURN_TIME = 5;

    private Tool mTool;
    private File mPhotoFile;
    private EditText mToolNameField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mReturnDateButton;
    private Button mReturnTimeButton;
    private CheckBox mReturnedCheckBox;
    private Button mContactButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    /**
     * Attaching arguments to a fragment must be done after the fragment is created but before it is
     * added to an activity. Thus, the conventional static method named newInstance(). This method
     * creates the fragment instance and bundles up and sets its arguments.
     *
     * @param toolId
     * @return
     */
    public static ToolFragment newInstance(UUID toolId){        //DP.4
        Bundle args = new Bundle();
        args.putSerializable(ARG_TOOL_ID, toolId);

        ToolFragment fragment = new ToolFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID toolId = (UUID) getArguments().getSerializable(ARG_TOOL_ID);
        mTool = ToolBox.get(getActivity()).getTool(toolId);
        mPhotoFile = ToolBox.get(getActivity()).getPhotoFile(mTool);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_tool, container, false);

        mToolNameField = (EditText) v.findViewById(R.id.tool_name);
        mToolNameField.setText(mTool.getToolName());
        mToolNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTool.setToolName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.tool_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {         //DatePicker.1
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mTool.getDate());                      //DP.5
                dialog.setTargetFragment(ToolFragment.this, REQUST_DATE);   //DP.7
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });
        mTimeButton = (Button) v.findViewById(R.id.tool_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mTool.getDate());
                dialog.setTargetFragment(ToolFragment.this, REQUEST_TIME);
                dialog.show(fragmentManager, DIALOG_TIME);
            }
        });

        mReturnDateButton = (Button) v.findViewById(R.id.return_tool_date);
        mReturnDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mTool.getReturnDate());
                dialog.setTargetFragment(ToolFragment.this, REQUEST_RETURN_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);

            }
        });

        mReturnTimeButton = (Button) v.findViewById(R.id.return_tool_time);
        mReturnTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mTool.getReturnDate());
                dialog.setTargetFragment(ToolFragment.this, REQUEST_RETURN_TIME);
                dialog.show(fragmentManager, DIALOG_TIME);
            }
        });

        setReturnButtons();
        mReturnedCheckBox = (CheckBox) v.findViewById(R.id.tool_returned_image);
        mReturnedCheckBox.setChecked(mTool.isReturned());
        mReturnedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isReturned) {
                mTool.setReturned(isReturned);
                setReturnButtons();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mContactButton = (Button) v.findViewById(R.id.contact);
        mContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if(mTool.getContact() != null){
            mContactButton.setText(mTool.getContact());
        }

        /**
         * This checks with part of the OS called the PackageManager to see if the user has a contacts app.
         * PackageManager knows about all the components installed on the android device, including all activities.
         * By calling resolveActivity(Intent, int), PackageManager finds an activity with the
         * CATEGORY_DEFAULT flag, just like startActivity(Intent) does.
         *
         * If this search is successful, PackageManager will return an instance of ResolveInfo.
         */
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mContactButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.tool_camera);
        final Intent captureImage= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.michaelportillo.android.toolcheckout.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for(ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.tool_photo);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){     //DP.10
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == REQUST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTool.setDate(date);
            updateDate();
        }else if (requestCode == REQUEST_TIME){
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mTool.setDate(date);
            updateTime();
        }else if (requestCode == REQUEST_CONTACT){
            Uri contactUri = data.getData();
            //Specify which fields you want your query to return values for
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //Perform your query - the contactUri is like a "where" clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try{
                //Double-check that you actually got results
                if(c.getCount() == 0){
                    return;
                }
                //Pull out the first column of the first row of data - that is your suspect's name
                c.moveToFirst();
                String contact = c.getString(0);
                mTool.setContact(contact);
                mContactButton.setText(contact);
            }finally{
                c.close();
            }
        }else if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.michaelportillo.android.toolcheckout.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }else if (requestCode == REQUEST_RETURN_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTool.setReturnDate(date);
            updateReturnDate();
        }else if (requestCode == REQUEST_RETURN_TIME){
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mTool.setReturnDate(date);
            updateReturnTime();
        }
    }

    private void updateTime() {
        mTimeButton.setText(DateFormat.format("h:mm a", mTool.getDate()).toString());
    }

    private void updateDate() {
        mDateButton.setText(DateFormat.format("EEE, MMM dd, yyyy", mTool.getDate()).toString());
    }

    private void updateReturnDate(){
        if(mTool.isReturned()){
            mReturnDateButton.setText(DateFormat.format("EEE, MMM dd, yyyy", mTool.getReturnDate()).toString());
        }
    }
    private void updateReturnTime(){
        if(mTool.isReturned()){
            mReturnTimeButton.setText(DateFormat.format("h:mm a", mTool.getReturnDate()).toString());
        }
    }

    private void setReturnButtons(){
        if(mTool.isReturned()){
            mReturnDateButton.setEnabled(true);
            mReturnTimeButton.setEnabled(true);
            mTool.setReturnDate(new Date());
        }else{
            mReturnDateButton.setEnabled(false);
            mReturnTimeButton.setEnabled(false);
        }
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_tool, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.delete_tool:
                ToolBox.get(getActivity()).removeTool(mTool);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *      d.9
     */
    @Override
    public void onPause(){
        super.onPause();
        ToolBox.get(getActivity()).updateTool(mTool);
    }
}
