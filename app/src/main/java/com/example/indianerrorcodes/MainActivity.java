package com.example.indianerrorcodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.release.indianerrorcodes.R;

public class MainActivity extends ActionBarActivity {
	RelativeLayout layout;
	TextView textview;
	EditText spn;
	String prevText;
	ArrayAdapter<String> dataAdapter;
	TestAdapter mDbHelper;
	int flag;
	ImageButton help;
	Spinner spinner;
	List<String> list;
	TextView fmiTextView;
	String fmiStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		layout = (RelativeLayout) findViewById(R.id.layout);
		textview = (TextView) findViewById(R.id.textView);
		fmiTextView = (TextView) findViewById(R.id.textView1);
		prevText = "";
		spn = (EditText) findViewById(R.id.edit_spn);
		flag = 1;
		list = new ArrayList<String>();
		mDbHelper = new TestAdapter(this);
		mDbHelper.createDatabase();
		textview.setTextSize(20);
		spinner = (Spinner) findViewById(R.id.spinner);
		help = (ImageButton) findViewById(R.id.popupButton);

		/* spinner listener */
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			// called when item in spinner is selected
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// An spinnerItem was selected. retrieve the selected item using
				// parent.getItemAtPosition(pos)
				fmiStr = (String) parent.getItemAtPosition(pos);
				flag = 0;
				((TextView) parent.getChildAt(0)).setTextColor(Color.RED);
				((TextView) parent.getChildAt(0)).setTextSize(18);
				onOKclick();

			}

			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing!
			}

		});

		/* spn textfield listener */
		spn.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String text = spn.getText().toString();
				if (!text.equals(prevText)) {
					flag = 1;
				}
				if (flag == 1) {
					mDbHelper.open();
					prevText = spn.getText().toString();
					if (spn.getText().toString() == null
							|| spn.getText().toString().equals("")) {
						return;
					}
					if(spn.getText().toString().length() >= 10){ return;}
					int spnInt = Integer.parseInt(spn.getText().toString());
					Cursor cursor = mDbHelper.getPossibleFMIsfromSPN(spnInt);
					addItemsOnSpinner(mDbHelper.convertFMIstoString(cursor));

					mDbHelper.close();
				}
			}
		});

		/*popup button listener*/
		help.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				LayoutInflater layoutInflater 
				= (LayoutInflater)getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);  
				View popupView = layoutInflater.inflate(R.layout.popup, null);  
				final PopupWindow popupWindow = new PopupWindow(
						popupView, 
						LayoutParams.WRAP_CONTENT,  
						LayoutParams.WRAP_CONTENT);  

				Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
				btnDismiss.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						popupWindow.dismiss();
					}});

				popupWindow.showAsDropDown(help, 50, -30);

			}});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onOKclick() {
		try {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
		} catch (Exception e) {
			// TODO: handle this exception somehow
		}
		mDbHelper.open();
		flag = 1;
		if(spn.getText().toString().equals("")){
			textview.setTextColor(Color.parseColor("#DF0101"));
			textview.setText("Error: You must enter an SPN!");
			return;
		}

		int spnInt = Integer.parseInt(spn.getText().toString());
		int fmiInt = Integer.parseInt(fmiStr);

		
		Cursor cursor = mDbHelper.getFromSPNandFMI(spnInt, fmiInt);
		if (!mDbHelper.convertRowtoString(cursor).equals("")) {
			textview.setTextColor(Color.parseColor("#ffffff"));
			textview.setText(Html.fromHtml(mDbHelper.convertRowtoString(cursor)));
		} else {
			textview.setTextColor(Color.parseColor("#DF0101"));
			textview.setText("Error: Invalid SPN/FMI combination");
		}
		mDbHelper.close();
	}

	public void spinnerClicked(View view) {
		int spnInt = Integer.parseInt(spn.getText().toString());
		Cursor cursorb = mDbHelper.getPossibleFMIsfromSPN(spnInt);
		addItemsOnSpinner(mDbHelper.convertFMIstoString(cursorb));
	}

	// add items into spinner dynamically
	public void addItemsOnSpinner(int[] intArr) {

		if (intArr.length == 0) {
			return;
		}
		Arrays.sort(intArr);
		String[] strArr = Arrays.toString(intArr).split("[\\[\\]]")[1]
				.split(", ");

		list = new ArrayList<String>();
		for (String item : strArr) {
			list.add(item);
		}
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
	}


}
