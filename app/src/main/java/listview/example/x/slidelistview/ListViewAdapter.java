package listview.example.x.slidelistview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Created by xww.
 * @Creation time 2018/8/21.
 */

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ContactEntity> mData;
    private SlideLayout mSlideLayout;

    ListViewAdapter(ArrayList<ContactEntity> data) {
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_list_item_content, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.civPhoto.setImageResource(mData.get(position).getmPhoto());
        holder.tvName.setText(mData.get(position).getmName());
        holder.tvMessage.setText(mData.get(position).getmMessage());

        holder.tvName.setOnClickListener(v -> Toast.makeText(parent.getContext(), "" + mData.get(position).getmName(), Toast.LENGTH_SHORT).show());
        holder.tvDelete.setOnClickListener(v -> {
            mData.remove(position);
            notifyDataSetChanged();
        });


        mSlideLayout = (SlideLayout) convertView;
        mSlideLayout.setOnSlideChangeListenr(new SlideLayout.onSlideChangeListenr() {
            @Override
            public void onMenuOpen(SlideLayout slideLayout) {
                mSlideLayout = slideLayout;
            }

            @Override
            public void onMenuClose(SlideLayout slideLayout) {
                if (mSlideLayout != null) {
                    mSlideLayout = null;
                }
            }

            @Override
            public void onClick(SlideLayout slideLayout) {
                if (mSlideLayout != null) {
                    mSlideLayout.closeMenu();
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        CircleImageView civPhoto;
        TextView tvName;
        TextView tvMessage;
        TextView tvDelete;

        ViewHolder(View itemView) {
            civPhoto = itemView.findViewById(R.id.item_image);
            tvName = itemView.findViewById(R.id.item_name);
            tvMessage = itemView.findViewById(R.id.item_message);
            tvDelete = itemView.findViewById(R.id.menu_delete);
        }
    }
}
