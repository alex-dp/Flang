package eu.depa.flang.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import eu.depa.flang.Constants;
import eu.depa.flang.R;
import eu.depa.flang.ResultLine;

public class ResultListAdapter extends ArrayAdapter<ResultLine> {

    final private Context context;
    final private int layoutResourceId;
    private ResultLine data[] = null;

    public ResultListAdapter(Context context, int layoutResourceId, ResultLine[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ResultHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ResultHolder();
            holder.from = (TextView) row.findViewById(R.id.from_in_result_line);
            holder.to = (TextView) row.findViewById(R.id.to_in_result_line);

            row.setTag(holder);
        } else {
            holder = (ResultHolder) row.getTag();
        }

        ResultLine line = data[position];
        holder.from.setText(line.from);
        holder.to.setText(line.to);

        if (line.correct != null) {
            holder.from.setTextColor(Constants.getColor(context,
                    (line.correct) ? R.color.green : R.color.red));
            holder.to.setTextColor(Constants.getColor(context,
                    (line.correct) ? R.color.green : R.color.red));
        }
        return row;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    static class ResultHolder {
        TextView from;
        TextView to;
    }
}