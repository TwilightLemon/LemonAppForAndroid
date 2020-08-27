package tk.twilightlemon.lemonapp.layouts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import tk.twilightlemon.lemonapp.Helpers.Settings;
import tk.twilightlemon.lemonapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            findPreference("cacheDownload").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SwitchPreference check=(SwitchPreference)preference;
                    Settings.ListenWithCache=check.isChecked();
                    SharedPreferences.Editor editor = Settings.sp;
                    editor.putBoolean("ListenWithCache",Settings.ListenWithCache);
                    return false;
                }
            });
            if(Settings.qq==""){
                findPreference("qq").setSummary("No Login");
                findPreference("name").setSummary("No Login");
                findPreference("usable").setSummary("g_tk Cookies 不可用");
            }else{
                findPreference("qq").setSummary("Has Login "+ Settings.qq);
                findPreference("name").setSummary(Settings.nick);
                findPreference("usable").setSummary("g_tk Cookies 有效");
            }
        }
    }
}