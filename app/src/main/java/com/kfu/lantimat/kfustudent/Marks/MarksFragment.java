package com.kfu.lantimat.kfustudent.Marks;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.OrientationHelper;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.kfu.lantimat.kfustudent.R;

        import java.util.ArrayList;


public class MarksFragment extends Fragment{

    RecyclerView recyclerView;
    MarksRecyclerAdapter marksRecyclerAdapter;
    static ArrayList<Mark> arMarks;

    public MarksFragment() {
        // Required empty public constructor
    }

    public static MarksFragment newInstance(ArrayList<Mark> arrayList) {
        MarksFragment fragment = new MarksFragment();
        arMarks = arrayList;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        marksRecyclerAdapter = new MarksRecyclerAdapter(arMarks);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        recyclerView.setAdapter(marksRecyclerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_marks, null);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        initRecyclerView();
        return v;

    }

}
