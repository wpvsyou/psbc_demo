package com.wp.demo.psbcdemo1.tools;
/*---------------------------------------------------------------------------------------------
 *                       Copyright (c) 2013 Capital Alliance Software(Pekall) 
 *                                    All Rights Reserved
 *    NOTICE: All information contained herein is, and remains the property of Pekall and
 *      its suppliers,if any. The intellectual and technical concepts contained herein are
 *      proprietary to Pekall and its suppliers and may be covered by P.R.C, U.S. and Foreign
 *      Patents, patents in process, and are protected by trade secret or copyright law.
 *      Dissemination of this information or reproduction of this material is strictly 
 *      forbidden unless prior written permission is obtained from Pekall.
 *                                     www.pekall.com
 *--------------------------------------------------------------------------------------------- 
 */


import android.content.Context;
import android.text.*;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wp.demo.psbcdemo1.psbccase.R;

public class EditTextWithDelete extends RelativeLayout implements
		View.OnClickListener, TextWatcher {

	private EditText mEditText;
	private ImageView mDelete;

	public EditTextWithDelete(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.mdm_edittext_with_delete,
				this, true);
		mEditText = (EditText) findViewById(R.id.mdm_edit);
		mEditText.addTextChangedListener(this);
		mDelete = (ImageView) findViewById(R.id.mdm_edit_delete);
		mDelete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mDelete) {
			mEditText.setText("");
		}
	}

	public void setText(String text) {
		mEditText.setText(text);
	}

	public String getText() {
		return mEditText.getText().toString();
	}

	public void setHint(String text) {
		mEditText.setHint(text);
	}

	public void setHint(int resId) {
		mEditText.setHint(resId);
	}

	public void setPassword(boolean password) {
		int position = mEditText.getSelectionStart();
		if (!password) {
			mEditText
					.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		} else {
			mEditText
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							16) });
			mEditText.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		mEditText.setSelection(position);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		boolean showDelete = (s != null && s.length() != 0) && isEnabled();
		mDelete.setVisibility(showDelete ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mEditText.setEnabled(enabled);
		String text = mEditText.getText().toString();
		boolean showDelete = !TextUtils.isEmpty(text) && enabled;
		mDelete.setVisibility(showDelete ? View.VISIBLE : View.GONE);
	}
}
