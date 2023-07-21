package algonquin.cst2335.pate0323.data.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import algonquin.cst2335.pate0323.data.MainActivityViewModel;
import algonquin.cst2335.pate0323.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding variableBinding;
    private MainActivityViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // calling onCreate from parent

        model = new ViewModelProvider(this).get(MainActivityViewModel.class);

        variableBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(variableBinding.getRoot());

        variableBinding.theButton.setOnClickListener(click -> {
            model.editString.postValue(variableBinding.theEditText.getText().toString());
        });
        model.editString.observe(this, s -> {
            variableBinding.theText.setText("Your edit text has " + s);
        });
        model.isSelected.observe(this, selected -> {
            variableBinding.CheckBox.setChecked((Boolean) selected);
            variableBinding.Switch.setChecked((Boolean) selected);
            variableBinding.RadioButton.setChecked((Boolean) selected);
        });
        variableBinding.CheckBox.setOnCheckedChangeListener((btn, isChecked) -> {
            model.isSelected.postValue(isChecked);
            showToast("CheckBox clicked");
        });
        variableBinding.CheckBox.setOnCheckedChangeListener((btn, isChecked) -> {
            model.isSelected.postValue(isChecked);
            showToast("Switch clicked");
        });
        variableBinding.CheckBox.setOnCheckedChangeListener((btn, isChecked) -> {
            model.isSelected.postValue(isChecked);
            showToast("RadioButton clicked");
        });
        variableBinding.TheImageButton.setOnClickListener(i -> {
            showToast("The width = " + i.getWidth() + "And the height = " + i.getHeight());
        });
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
