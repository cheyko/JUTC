package com.jutcjm.jutc;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class BusCheckFragment extends DialogFragment {

    private static final String TAG = "MyCustomDialog1";

    public interface OnInputListener{
        void sendInput(int numero);
    }

    public OnInputListener mOnInputListener;

    private Button searchbutton;
    private Button cancelbutton;
    private EditText numEntered;
    private Integer num;

    public BusCheckFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bus_check, container, false);

        searchbutton = (Button) v.findViewById(R.id.searchButton);
        cancelbutton = (Button) v.findViewById(R.id.cancelButton);

        numEntered = (EditText) v.findViewById(R.id.numInput);

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Search was cancelled", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    num = Integer.parseInt(numEntered.getText().toString());
                }catch (NullPointerException e){

                    Toast.makeText(getContext(), "No number was entered ", Toast.LENGTH_LONG).show();
                }

                mOnInputListener.sendInput(num);

                getDialog().dismiss();

/*                Intent newIntent = new Intent(getActivity(), MapsActivity.class);
                newIntent.putExtra("busNumber1234",num);
                startActivity(newIntent);*/
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{

            mOnInputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }

    }
}
