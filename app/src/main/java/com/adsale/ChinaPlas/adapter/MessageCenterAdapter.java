package com.adsale.ChinaPlas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.data.model.MessageCenter;

import java.util.ArrayList;

public class MessageCenterAdapter extends RecyclerView.Adapter<MessageCenterAdapter.MessageViewHolder>{
	private ArrayList<MessageCenter.Message> messages;

	public MessageCenterAdapter(ArrayList<MessageCenter.Message> lists) {
		this.messages=lists;
	}

	@Override
	public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_center,viewGroup,false);
		return new MessageViewHolder(view);
	}

	@Override
	public void onBindViewHolder(MessageViewHolder messageViewHolder, int position) {
		messageViewHolder.tv_message.setText(messages.get(position).Message);
		messageViewHolder.tv_time.setText(messages.get(position).date);
	}

	@Override
	public int getItemCount() {
		return messages.size();
	}

	static class MessageViewHolder extends RecyclerView.ViewHolder{
		private TextView tv_message;
		private TextView tv_time;

		 MessageViewHolder(View itemView) {
			super(itemView);
			tv_message = (TextView) itemView.findViewById(R.id.tv_message);
			tv_time = (TextView) itemView.findViewById(R.id.tv_message_time);
		}
	}

}
