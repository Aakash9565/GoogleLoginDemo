package googlelogindemo.androidbeasts.com.googlelogindemoadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import googlelogindemo.androidbeasts.com.googlelogindemo.ProfileActivity;
import googlelogindemo.androidbeasts.com.googlelogindemo.R;

/**
 * Created by aakash on 22/08/15.
 */
public class CustomAdapter extends BaseAdapter{
    ArrayList<String> names,images;
    Context context;
    LayoutInflater inflater;
    public CustomAdapter(ProfileActivity profileActivity, ArrayList<String> friendnames,ArrayList<String> friendImages){
        context=profileActivity;
        names=new ArrayList<>();
        images=new ArrayList<>();
        names=friendnames;
        images=friendImages;
        inflater=( LayoutInflater ) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        if(names.size()<=0)
            return 1;
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public static class ViewHolder{

        public TextView textName;
        public ImageView usersImage;


    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=new ViewHolder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_item, null);
        holder.textName=(TextView) rowView.findViewById(R.id.userName);
        holder.usersImage=(ImageView) rowView.findViewById(R.id.userImage);
        holder.textName.setText(names.get(position));
        Picasso.with(context).load(images.get(position)).transform(new CircleTransform()).placeholder(R.drawable.icon_profile).error(R.drawable.icon_profile).resize(60, 60).into(holder.usersImage);
        return rowView;
    }


}
