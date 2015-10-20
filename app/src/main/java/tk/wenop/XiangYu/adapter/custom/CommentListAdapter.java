package tk.wenop.XiangYu.adapter.custom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.bean.CommentEntity;
import tk.wenop.XiangYu.ui.activity.CommentViewActivity;

public class CommentListAdapter extends BaseAdapter implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{

    private List<CommentEntity> allCommentEntity = new ArrayList<>();
    private List<CommentEntity> filter_CommentEntity = new ArrayList<>();
//    DBManager dbManager = DBManager.getInstance();



    public void putCommentEntity(List<CommentEntity> allComment){

        if (allComment == null) return;
        if (allComment.size() < 0) return;

        if (allComment.size()<=allCommentEntity.size()) {
            filter_CommentEntity.addAll(allCommentEntity);
            notifyDataSetChanged();
            return;
        }else if (allComment.size() > allCommentEntity.size()){
            allCommentEntity.clear();
            allCommentEntity.addAll(allComment);
            filter_CommentEntity.clear();
            filter_CommentEntity.addAll(allComment);
        }

        notifyDataSetChanged();
    }

    Context context;

    public CommentListAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return filter_CommentEntity.size();
    }

    @Override
    public Object getItem(int position) {
        return filter_CommentEntity.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final CommentEntity  commentEntity = (CommentEntity)getItem(position);
        CommentHolder commentHolder = new CommentHolder();

        if(convertView == null){

            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment_list,parent,false);


			commentHolder.comment = (ImageView) convertView.findViewById(R.id.comment);
			


            convertView.setTag(commentHolder);
        }else {
            commentHolder = (CommentHolder) convertView.getTag();
        }


//		commentHolder.comment.setText(commentEntity.getComment());
		

        //if (dbManager.commentEntityCarFileHashMap.containsKey(commentEntity.getObjectId())){
        //    CarFile file = dbManager.carEntityCarFileHashMap.get(carEntity.getObjectId());
        //    file.getFile().loadImage(context,carHolder.imageView);
        //}

        convertView.setClickable(true);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentViewActivity.class);
                CommentViewActivity.commentEntity = commentEntity;
                context.startActivity(intent);
            }
        });


//        commentHolder.cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                delete(position);
//            }
//        });



        //if (MainFrontActivity.nearstOrMine == 0){
        //    commentHolder.cancel.setVisibility(View.INVISIBLE);
        //}else {
        //    commentHolder.cancel.setVisibility(View.VISIBLE);
        //}



        return convertView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



    }

    public void delete(final int pos){


    }

    public static class CommentHolder{



        public ImageView comment;



    }





    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


     //   final int pos = position;
     //   Dialog dialog = new Dialog(context,"delete","Are you sure?");
     //   dialog.setCancelable(true);
     //   dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
     //       @Override
     //       public void onClick(View v) {
     //           CommentEntity commentEntity = (CommentEntity) getItem(pos);
     //          commentEntity.delete(context);
     //         filter_CommentEntity.remove(commentEntity);
     //           allCommentEntity.remove(commentEntity);
     //           putCommentEntity(filter_CommentEntity);
     //          dbManager.removeCommentEntity(commentEntity);
     //       }
     //   });

     //   dialog.show();
      //  dialog.show();

        return false;
    }

    public static class ClickHolder{
        int position;
        TextView numberTextView;
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        filter_CommentEntity.clear();
        if (charText.length() == 0) {
            filter_CommentEntity.addAll(allCommentEntity);
        }
        else
        {
            for (CommentEntity wp : allCommentEntity)
            {
//                if (wp.getCommentType().toLowerCase(Locale.getDefault()).contains(charText))
//                {
//                    filter_CommentEntity.add(wp);
//                }
            }
        }
        putCommentEntity(filter_CommentEntity);
    }


        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ClickHolder clickHolder = (ClickHolder) v.getTag();
             final int position =  clickHolder.position;

            }
        };
    }
    