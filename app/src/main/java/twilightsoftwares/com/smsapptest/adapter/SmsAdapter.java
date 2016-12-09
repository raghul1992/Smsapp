package twilightsoftwares.com.smsapptest.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import twilightsoftwares.com.smsapptest.R;
import twilightsoftwares.com.smsapptest.modal.SmsDisplayModal;

/**
 * Created by twilightuser on 7/12/16.
 */
public class SmsAdapter extends BaseAdapter implements Filterable{
    Context context;
    List<SmsDisplayModal> rowItem,rowItemTemp;
    public SmsAdapter(Context context, List<SmsDisplayModal> rowItem) {
        this.context = context;
        this.rowItem = rowItem;
        this.rowItemTemp=rowItem;
    }
    @Override
    public int getCount() {
        return rowItem.size();
    }
    @Override
    public Object getItem(int position) {return position;}
    @Override
    public long getItemId(int position) {return position;}
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.sms_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        SmsDisplayModal row_pos = rowItem.get(position);
        if (row_pos != null) {
            holder.name.setText(row_pos.name);
            holder.msg.setText(row_pos.message);
            holder.msg.post(new Runnable() {
                @Override
                public void run() {
                    int lineCnt = holder.msg.getLineCount();

                    if (lineCnt > 4) {
                        holder.more.setVisibility(View.VISIBLE);
                    } else {
                        holder.more.setVisibility(View.GONE);
                    }
                }
            });
        }
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((holder.more).getText().toString()).equalsIgnoreCase("View more") && (holder.msg.getLineCount() > 3)) {
                    holder.msg.setMaxLines(20);
                    holder.more.setText("View less");
                } else {
                    holder.msg.setMaxLines(3);
                    holder.more.setText("View more");
                }

            }
        });
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.tv_name) TextView name;
        @BindView(R.id.tv_message) TextView msg;
        @BindView(R.id.tv_more) TextView more;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<SmsDisplayModal> FilteredArrList = new ArrayList<SmsDisplayModal>();

                if(rowItemTemp == null){

                    rowItemTemp = new ArrayList<SmsDisplayModal>(rowItem);
                }

                if (constraint == null || constraint.length() == 0) {

                    results.count = rowItemTemp.size();
                    results.values = rowItemTemp;

                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < rowItemTemp.size(); i++) {
                        String data = rowItemTemp.get(i).message;
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(rowItemTemp.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {

                rowItem = (ArrayList<SmsDisplayModal>) results.values;
                notifyDataSetChanged();

            }
        };

        return filter;
    }


}
