package org.openmrs.client.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.openmrs.client.R;
import org.openmrs.client.dao.PatientDAO;
import org.openmrs.client.models.Patient;

import java.util.List;

public class FindPatientArrayAdapter extends ArrayAdapter<Patient> {
    private Activity mContext;
    private List<Patient> mItems;
    private int mResourceID;

    class ViewHolder {
        private TextView mIdentifier;
        private TextView mDisplayName;
        private TextView mGender;
        private TextView mAge;
        private TextView mBirthDate;
        private CheckBox mAvailableOfflineCheckbox;
    }

    public FindPatientArrayAdapter(Activity context, int resourceID, List<Patient> items) {
        super(context, resourceID, items);
        this.mContext = context;
        this.mItems = items;
        this.mResourceID = resourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            rowView = inflater.inflate(mResourceID, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mIdentifier = (TextView) rowView.findViewById(R.id.patientIdentifier);
            viewHolder.mDisplayName = (TextView) rowView.findViewById(R.id.patientDisplayName);
            viewHolder.mGender = (TextView) rowView.findViewById(R.id.patientGender);
            viewHolder.mAge = (TextView) rowView.findViewById(R.id.patientAge);
            viewHolder.mBirthDate = (TextView) rowView.findViewById(R.id.patientBirthDate);
            viewHolder.mAvailableOfflineCheckbox = (CheckBox) rowView.findViewById(R.id.offlineCheckbox);
            rowView.setTag(viewHolder);
        }

        // fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final Patient patient = mItems.get(position);
        if (null != patient.getIdentifier()) {
            holder.mIdentifier.setText("#" + patient.getIdentifier());
        }
        if (null != patient.getDisplay()) {
            holder.mDisplayName.setText(patient.getDisplay());
        }
        if (null != patient.getGender()) {
            holder.mGender.setText(patient.getGender());
        }
        if (null != patient.getAge()) {
            holder.mAge.setText(patient.getAge());
        }
        String birthDate = patient.getBirthDate();
        if (null != birthDate) {
            holder.mBirthDate.setText(birthDate.substring(0, birthDate.indexOf('T')));
        }
        if (null != holder.mAvailableOfflineCheckbox) {
            if (new PatientDAO().userDoesNotExist(patient.getUuid())) {
                holder.mAvailableOfflineCheckbox.setText(mContext.getString(R.string.find_patients_row_checkbox_download_label));
                holder.mAvailableOfflineCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((CheckBox) v).isChecked()) {
                            new PatientDAO().savePatient(patient);
                            Toast.makeText(mContext, R.string.action_settings, Toast.LENGTH_SHORT).show();
                            disableCheckBox(holder);
                        }
                    }
                });
            } else {
                disableCheckBox(holder);
            }
        }
        return rowView;
    }

    public void disableCheckBox(ViewHolder holder) {
        holder.mAvailableOfflineCheckbox.setChecked(true);
        holder.mAvailableOfflineCheckbox.setClickable(false);
        holder.mAvailableOfflineCheckbox.setText(mContext.getString(R.string.find_patients_row_checkbox_available_offline_label));
    }
}
