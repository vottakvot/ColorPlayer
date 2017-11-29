package ru.testsimpleapps.coloraudioplayer.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.ui.fragments.BaseFragment;

public abstract class BaseActivity extends AppCompatActivity {

    protected FragmentManager mFragmentManager;
    protected Snackbar mSnackBar;
    protected Toast mToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        boolean handled = false;
        final List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment) {
                    handled = ((BaseFragment) fragment).onBackPressed();

                    // Check child fragments
                    // Inner back press has higher priority
                    final List<Fragment> childFragmentList = fragment.getChildFragmentManager().getFragments();
                    if (childFragmentList != null) {
                        for (Fragment childFragment : childFragmentList) {
                            if (childFragment instanceof BaseFragment) {
                                final BaseFragment baseFragment = (BaseFragment) childFragment;
                                // Todo: add check for visibility
                                if (baseFragment.onBackPressed()) {
                                    return;
                                }
                            }
                        }
                    }

                    // Inner back press in fragment
                    if (handled) {
                        break;
                    }
                }
            }
        }

        // Outer back press
        if (!handled) {
            super.onBackPressed();
        }
    }

    private void init(final Bundle savedInstanceState) {
        setSnackBar();
        setToast();
    }

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

    private void setSnackBar() {
        mSnackBar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT);
        mSnackBar.setDuration(Snackbar.LENGTH_SHORT);
    }

    private void setToast() {
        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        mToast.setDuration(Toast.LENGTH_SHORT);
    }

    protected void showSnackBar(final int resource) {
        mSnackBar.setText(getResources().getString(resource));
        mSnackBar.show();
    }

    protected void showSnackBar(final String string) {
        mSnackBar.setText(string);
        mSnackBar.show();
    }

    protected void showToast(final int resource) {
        mToast.setText(getResources().getString(resource));
        mToast.show();
    }

    protected void showToast(final String string) {
        mToast.setText(string);
        mToast.show();
    }

}
