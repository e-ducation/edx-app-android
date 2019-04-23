package org.edx.mobile.eliteu.professor;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.edx.mobile.base.BaseSingleFragmentActivity;

public class ProfessorDetailActivity extends BaseSingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("professor_name"));
    }

    @Override
    public Fragment getFirstFragment() {
        return ProfessorDetailFragment.newInstance(getIntent().getIntExtra("professor_id",0));
    }

}
