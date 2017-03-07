package com.gmail.dleemcewen.tandemfieri.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gmail.dleemcewen.tandemfieri.Entities.Rating;
import com.gmail.dleemcewen.tandemfieri.Entities.Restaurant;
import com.gmail.dleemcewen.tandemfieri.R;
import com.gmail.dleemcewen.tandemfieri.Repositories.Ratings;

import java.util.List;

/**
 * RestaurantRatingsListAdapter provides the required methods to render the
 * listview used in the restaurant ratings activity
 */
public class RestaurantRatingsListAdapter extends BaseAdapter {
    private Activity context;
    private Resources resources;
    private List<Restaurant> restaurantsList;
    private Ratings<Rating> ratingsRepository;

    public RestaurantRatingsListAdapter(Activity context, List<Restaurant> restaurantsList) {
        this.context = context;
        resources = context.getResources();
        this.restaurantsList = restaurantsList;
        ratingsRepository = new Ratings<>(context);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return restaurantsList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return restaurantsList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the
     * underlying data.
     *
     * @return whether or not the same ID always refers to the same object
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Restaurant restaurant = (Restaurant)getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.restaurant_ratings_list_item, null);
        }

        TextView rateableRestaurantName = (TextView)convertView.findViewById(R.id.restaurantToRate);
        rateableRestaurantName.setText(restaurant.getName());

        /*TextView restaurantDriverName = (TextView)convertView.findViewById(R.id.restaurantDriverName);
        restaurantDriverName.setText(user.getFirstName() + " " + user.getLastName());
        restaurantDriverName.setTypeface(null, Typeface.BOLD);

        Button removeRestaurantDriver = (Button)convertView.findViewById(R.id.removeRestaurantDriver);
        removeRestaurantDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder dialogMessageBuilder = new StringBuilder();
                dialogMessageBuilder.append(resources.getString(R.string.removeConfirmationQuestion));
                dialogMessageBuilder.append(" ");
                dialogMessageBuilder.append(user.getFirstName());
                dialogMessageBuilder.append(" ");
                dialogMessageBuilder.append(user.getLastName());
                dialogMessageBuilder.append(" from the drivers assigned to this restaurant?");

                AlertDialog.Builder removalConfirmationDialog  = new AlertDialog.Builder(context);
                removalConfirmationDialog
                        .setMessage(dialogMessageBuilder.toString());
                removalConfirmationDialog
                        .setTitle(resources.getString(R.string.manageRestaurantDriversActivityTitle));
                removalConfirmationDialog.setCancelable(false);
                removalConfirmationDialog.setPositiveButton(
                        resources.getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                restaurantDriversList.remove(user);

                                user.setRestaurantId(null);
                                usersRepository.update(user, new String[] {"Driver"});

                                ((ManageRestaurantDrivers)context).getAssignedRestaurantDrivers();

                                dialog.cancel();
                            }
                        });
                removalConfirmationDialog.setNegativeButton(
                        resources.getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                removalConfirmationDialog
                        .create()
                        .show();
            }
        });*/

        return convertView;
    }
}

