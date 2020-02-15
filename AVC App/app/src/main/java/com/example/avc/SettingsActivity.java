package com.example.avc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference myPref = (Preference) findPreference("favoriteContact");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(i, 7);
                    return true;
                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            // TODO Auto-generated method stub
            super.onActivityResult(requestCode, resultCode, data);
            String TempNameHolder, TempNumberHolder, TempContactID, IDresult = "" ;
            String phoneNum = "";
            int IDresultHolder ;
            Preference myPref = (Preference) findPreference("favoriteContact");

            if (resultCode == Activity.RESULT_OK && requestCode == 7) {

                Uri uri = data.getData();
                Cursor cursor1 = getContext().getContentResolver().query(uri, null,
                        null, null, null);
                if (cursor1.moveToFirst()) {
                    TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));

                    IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    IDresultHolder = Integer.valueOf(IDresult) ;

                    if (IDresultHolder == 1) {

                        Cursor cursor2 = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null);

                        while (cursor2.moveToNext()) {

                            TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            phoneNum = TempNumberHolder;
                        }
                }
                    Toast.makeText(getActivity().getBaseContext(), "Număr de contact: "+phoneNum, Toast.LENGTH_LONG).show();
                    myPref.setSummary("Număr de contact: " + phoneNum);
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("favoriteContact", MODE_PRIVATE).edit();
                    editor.putString("phoneNum", phoneNum);
                     editor.apply();
            }
            }
        }
    }
}