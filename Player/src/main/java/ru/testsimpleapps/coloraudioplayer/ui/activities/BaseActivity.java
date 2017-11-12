package ru.testsimpleapps.coloraudioplayer.ui.activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.ui.fragments.BaseFragment;

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

    @Override
    public void onBackPressed() {
        boolean handled = false;
        final List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment) {
                    handled = ((BaseFragment)fragment).onBackPressed();

                    // Check child fragments
                    // Inner back press has higher priority
                    final List<Fragment> childFragmentList = fragment.getChildFragmentManager().getFragments();
                    if (childFragmentList != null) {
                        for (Fragment childFragment : childFragmentList) {
                            if (childFragment instanceof BaseFragment) {
                                final BaseFragment baseFragment = (BaseFragment)childFragment;
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
        if(!handled) {
            super.onBackPressed();
        }
    }

}
