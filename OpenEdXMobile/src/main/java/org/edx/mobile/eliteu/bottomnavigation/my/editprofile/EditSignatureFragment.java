package org.edx.mobile.eliteu.bottomnavigation.my.editprofile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseFragment;
import org.edx.mobile.user.FormField;
import org.edx.mobile.view.FormFieldActivity;

public class EditSignatureFragment extends BaseFragment implements TextWatcher {

    TextView textView;
    String before = "";
    MenuItem item;
    SpannableString spannableString;
    boolean canClick = false;
    FormField formField;
    EditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_signature, container, false);
        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        formField = (FormField) getActivity().getIntent().getSerializableExtra(FormFieldActivity.EXTRA_FIELD);
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(formField.getLabel());
        editText = view.findViewById(R.id.text);
        editText.addTextChangedListener(this);
        textView = view.findViewById(R.id.text_length);
        before = getActivity().getIntent().getStringExtra(FormFieldActivity.EXTRA_VALUE);
        if (before == null) {
            before = "";
        }
        editText.setText(before);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String EditableS = s.toString();
        int s_lenth = EditableS.length();
        if (s_lenth <= 200) {
            textView.setText(s_lenth + "/200");
            if (before.equals(EditableS) || s_lenth == 0) {
                if (spannableString != null) {
                    spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.new_bind_mobile_get_code_cannot_click)), 0, spannableString.length(), 0);
                    item.setTitle(spannableString);
                    canClick = false;
                }
            } else {
                if (spannableString != null) {
                    spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.new_bind_mobile_get_code_can_click)), 0, spannableString.length(), 0);
                    item.setTitle(spannableString);
                    canClick = true;
                }
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu_submit, menu);
        item = menu.findItem(R.id.menu_onclick);
        spannableString = new SpannableString(item.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.new_bind_mobile_get_code_cannot_click)), 0, spannableString.length(), 0);
        item.setTitle(spannableString);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_onclick: {
                menuOnclick();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void menuOnclick() {
        if (canClick) {
            getActivity().setResult(Activity.RESULT_OK,
                    new Intent()
                            .putExtra(FormFieldActivity.EXTRA_FIELD, formField)
                            .putExtra(FormFieldActivity.EXTRA_VALUE, editText.getText().toString()));
            getActivity().finish();
        }

    }
}
