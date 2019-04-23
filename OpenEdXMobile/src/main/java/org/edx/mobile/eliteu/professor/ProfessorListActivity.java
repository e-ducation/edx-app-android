package org.edx.mobile.eliteu.professor;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.edx.mobile.R;
import org.edx.mobile.base.BaseSingleFragmentActivity;

public class ProfessorListActivity extends BaseSingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.professor_list_title);
    }

    @Override
    public Fragment getFirstFragment() {
        return new ProfessorListFragment();
    }

}
