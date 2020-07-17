package com.genecare.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.genecare.R;
import com.genecare.databinding.CommentRowBinding;
import com.genecare.databinding.LoadMoreRowBinding;
import com.genecare.models.CommentDataModel;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_DATA = 1;
    private final int LOAD = 2;
    private List<CommentDataModel.CommentModel> commentModelList;
    private Context context;
    private LayoutInflater inflater;

    public CommentAdapter(List<CommentDataModel.CommentModel> commentModelList, Context context) {
        this.commentModelList = commentModelList;
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==ITEM_DATA)
        {
            CommentRowBinding binding  = DataBindingUtil.inflate(inflater, R.layout.comment_row,parent,false);
            return new CommentHolder(binding);

        }else
            {
                LoadMoreRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.load_more_row,parent,false);
                return new LoadHolder(binding);
            }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentDataModel.CommentModel commentModel = commentModelList.get(position);
        if (holder instanceof CommentHolder)
        {
            CommentHolder commentHolder = (CommentHolder) holder;
            commentHolder.binding.setCommentModel(commentModel);



        }else
            {
                LoadHolder loadHolder = (LoadHolder) holder;
                loadHolder.binding.progBar.setIndeterminate(true);
            }

    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        public CommentRowBinding binding;
        public CommentHolder(@NonNull CommentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class LoadHolder extends RecyclerView.ViewHolder {
        private LoadMoreRowBinding binding;
        public LoadHolder(@NonNull LoadMoreRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }

    }

    @Override
    public int getItemViewType(int position) {
        CommentDataModel.CommentModel commentModel = commentModelList.get(position);
        if (commentModel!=null)
        {
            return ITEM_DATA;
        }else
            {
                return LOAD;
            }

    }


}
