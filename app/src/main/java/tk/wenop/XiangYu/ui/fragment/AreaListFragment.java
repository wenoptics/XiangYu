package tk.wenop.XiangYu.ui.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import cn.bmob.v3.BmobUser;
import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.adapter.custom.AreaListAdapter;
import tk.wenop.XiangYu.bean.AreaEntity;
import tk.wenop.XiangYu.bean.User;
import tk.wenop.XiangYu.network.AreaNetwork;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AreaListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AreaListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AreaListFragment extends Fragment implements AreaNetwork.OnGetFollowAreaEntities {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    @ViewInject(R.id.list)
    ListView listView;

    AreaListAdapter areaListAdapter;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AreaListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AreaListFragment newInstance() {
        AreaListFragment fragment = new AreaListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public AreaListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


         areaListAdapter = new AreaListAdapter(getActivity());
        //todo: 加载arealist
        User user = BmobUser.getCurrentUser(getActivity(), User.class);
        AreaNetwork.loadFollowAreas(getActivity(), this, user);
        listView.setAdapter(areaListAdapter);

    }


    //得到关联的地点
    @Override
    public void onGetFollowAreaEntities(List<AreaEntity> areaEntities) {
        areaListAdapter.putAreaEntity(areaEntities);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_area_list, container, false);
        listView = (ListView) view.findViewById(R.id.list);

        ViewUtils.inject(view);


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
