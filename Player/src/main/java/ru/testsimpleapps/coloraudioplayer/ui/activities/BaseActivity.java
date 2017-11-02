package ru.testsimpleapps.coloraudioplayer.ui.activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.testsimpleapps.coloraudioplayer.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected FragmentManager mFragmentManager;

    protected void showFragment(@NonNull final Fragment fragment, @Nullable final String tag) {

        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        if (fragment != null) {
            final FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fragment_open,
                    R.anim.fragment_close,
                    R.anim.fragment_open,
                    R.anim.fragment_close).replace(R.id.frame_container, fragment);

            if (tag != null) {
                fragmentTransaction.addToBackStack(tag);
            }

            fragmentTransaction.commit();
        }
    }

}
