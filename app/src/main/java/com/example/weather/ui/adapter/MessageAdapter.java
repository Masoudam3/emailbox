package com.example.weather.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.weather.R;
import com.example.weather.model.Message;
import com.example.weather.utils.App;
import com.example.weather.utils.CircleTransform;
import com.example.weather.utils.FlipAnimator;
import com.example.weather.utils.MdColorUtils;
import java.util.ArrayList;
import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MsgViewHolder> {

    MessageAdapterListener listener;
    List<Message> messages;
    SparseBooleanArray selectedItems;
    SparseBooleanArray animationItemsIndex;
    boolean reverseAllAnimations = false;
    int currentSelectedIndex = -1;

    public MessageAdapter(List<Message> messages, MessageAdapterListener listener) {
        this.messages = messages;
        if (messages == null) {
            this.messages = new ArrayList<>();
        }
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        this.listener = listener;
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_item, parent, false);
        return new MsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {
        Message msg = messages.get(position);
        //
        if (msg.getColor() == -1) {
            msg.setColor(MdColorUtils.randomColor(holder.from.getContext()));
        }
        // details
        holder.from.setText(msg.getFrom());
        holder.subject.setText(msg.getSubject());
        holder.message.setText(msg.getMessage());
        holder.timestamp.setText(msg.getTimestamp());

        // first letter of 'from' -> icon_text
        holder.iconText.setText(msg.getFrom().substring(0, 1));

        //
        holder.itemView.setActivated(selectedItems.get(position, false));
        //
        applyIsRead(holder, msg);
        applyImportant(holder, msg);
        applyProfileIcon(holder, msg);
        applyClickEvents(holder, position);
        applyIconAnimation(holder, position);
    }

    private void applyIconAnimation(MsgViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetAxisY(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1f);
            if (currentSelectedIndex == position) {
                FlipAnimator.flip(holder.iconBack.getContext(), holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetAxisY(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1f);
            if (currentSelectedIndex == position ||
                    (reverseAllAnimations && animationItemsIndex.get(position, false))) {
                FlipAnimator.flip(holder.iconBack.getContext(), holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }

    }

    private void resetAxisY(View v) {
        if (v.getRotationY() != 0) {
            v.setRotationY(0);
        }
    }

    public void resetAnimations() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    private void applyClickEvents(MsgViewHolder holder, final int position) {
        holder.iconLayout.setOnClickListener(v -> listener.onIconClicked(position));

        holder.messageLayout.setOnClickListener(v -> listener.onMessageClicked(position));

        holder.messageLayout.setOnLongClickListener(v -> {
            listener.onRowLongClicked(position);
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        });

        holder.iconStar.setOnClickListener(v -> listener.onIconImportantClicked(position));

    }

    private void applyProfileIcon(MsgViewHolder holder, Message msg) {
        String picture = msg.getPicture();

        if (!TextUtils.isEmpty(picture)) {

            switch (picture){

                case "google.png":

                     LoadImage( holder,  App.GOOGLE);
                    break;
                case "imdb.png":
                    LoadImage( holder,  App.IMDB);
                    break;

                case "profile.jpg":
                    LoadImage( holder,  App.PROFILE);
                    break;

                default:break;
            }
            holder.iconProfile.setColorFilter(null);
            holder.iconText.setVisibility(View.GONE);

        } else {
            holder.iconProfile.setImageResource(R.drawable.bg_circle);
            holder.iconProfile.setColorFilter(msg.getColor());
            holder.iconText.setVisibility(View.VISIBLE);
        }
    }




    private void LoadImage(MsgViewHolder holder, String url) {

        Glide.with(holder.iconProfile.getContext())
                .load(url)
                .thumbnail(0.5f)
                .centerCrop()
                .transform(new CircleTransform())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iconProfile);
    }


    private void applyImportant(MsgViewHolder holder, Message msg) {
        if(msg.isImportant()){
            holder.iconStar.setImageResource(R.drawable.ic_star_black_24dp);
            holder.iconStar.setColorFilter(
                    ContextCompat.getColor(holder.iconStar.getContext(), R.color.star_tint_selected));
        } else {
            holder.iconStar.setImageResource(R.drawable.ic_star_border_black_24dp);
            holder.iconStar.setColorFilter(
                    ContextCompat.getColor(holder.iconStar.getContext(), R.color.star_tint_default));
        }
    }

    private void applyIsRead(MsgViewHolder holder, Message msg) {
        if(msg.isRead()){
            holder.from.setTypeface(null, Typeface.NORMAL);
            holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.from.setTextColor(ContextCompat.getColor(
                    holder.from.getContext(), R.color.txt_subject_color));
            holder.subject.setTextColor(ContextCompat.getColor(
                    holder.subject.getContext(), R.color.txt_subject_color));

        } else {
            holder.from.setTypeface(null, Typeface.BOLD);
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.from.setTextColor(ContextCompat.getColor(
                    holder.from.getContext(), R.color.txt_from_color));
            holder.subject.setTextColor(ContextCompat.getColor(
                    holder.subject.getContext(), R.color.txt_from_color));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void toggleSelection(int pos){
        currentSelectedIndex = pos;
        if(selectedItems.get(pos, false)){
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelections(){
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedItems(){
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for(int i = 0; i < selectedItems.size() ; i++){
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeData(int pos){
        messages.remove(pos);
        resetCurrentIndex();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount(){
        return selectedItems.size();
    }

    private void resetCurrentIndex(){
        currentSelectedIndex = -1;
    }

    class MsgViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView from, subject, message, timestamp, iconText;
        ImageView iconStar, iconProfile;
        LinearLayout messageLayout;
        FrameLayout iconLayout, iconBack, iconFront;
        MsgViewHolder(View itemView){
            super(itemView);
            from =  itemView.findViewById(R.id.from);
            subject =  itemView.findViewById(R.id.subject);
            message = itemView.findViewById(R.id.message);
            timestamp =  itemView.findViewById(R.id.timestamp);
            iconText = itemView.findViewById(R.id.icon_text);
            iconStar = itemView.findViewById(R.id.icon_star);
            iconProfile = itemView.findViewById(R.id.icon_profile);
            messageLayout =itemView.findViewById(R.id.message_layout);
            iconLayout =  itemView.findViewById(R.id.icon_layout);
            iconBack =  itemView.findViewById(R.id.icon_back);
            iconFront = itemView.findViewById(R.id.icon_front);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onRowLongClicked(getAdapterPosition());
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public interface MessageAdapterListener{
        void onMessageClicked(int position);
        void onIconImportantClicked(int position);
        void onRowLongClicked(int position);
        void onIconClicked(int position);
    }
}
