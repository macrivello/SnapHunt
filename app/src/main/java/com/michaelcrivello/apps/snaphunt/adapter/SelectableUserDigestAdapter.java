package com.michaelcrivello.apps.snaphunt.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.inject.Inject;
import com.michaelcrivello.apps.snaphunt.data.model.UserDigest;
import com.michaelcrivello.apps.snaphunt.exception.TooManyItemsSelectedException;
import com.michaelcrivello.apps.snaphunt.misc.Selectable;
import com.michaelcrivello.apps.snaphunt.util.Constants;
import com.michaelcrivello.apps.snaphunt.util.UserManager;
import com.michaelcrivello.apps.snaphunt.view.UserDigestListItemView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import roboguice.RoboGuice;
import roboguice.util.Ln;

/**
 * Created by tao on 6/18/15.
 */
public class SelectableUserDigestAdapter extends UserDigestAdapter implements SelectableAdapter {
    private static final int MAX_SELECTABLE_DEFAULT = 10;

    @Inject UserManager userManager;
    HashMap<String, Selectable> selectables;
    ArrayList<Selectable> selected;
    int maxSelection;

    public SelectableUserDigestAdapter(Context context) {
        this(context, MAX_SELECTABLE_DEFAULT);
        RoboGuice.injectMembers(context, this);
        selectables = new HashMap<String, Selectable>();
        selected = new ArrayList<>();
    }

    public SelectableUserDigestAdapter(Context context, int maxSelection) {
        super(context);
        RoboGuice.injectMembers(context, this);
        this.maxSelection = maxSelection;
        selectables = new HashMap<String, Selectable>();
        selected = new ArrayList<>();
    }


    @Override
    public List<?> getSelectedItems() {
        return selected;
    }

    @Override
    public Selectable getSelectable(String userDigestId) {
        return selectables.get(userDigestId);
    }

    @Override
    public void setSelected(Selectable selectable, boolean selectedState) throws TooManyItemsSelectedException {
        // Check if there is room
        if (selected.size() + 1 > this.maxSelection) {
            throw new TooManyItemsSelectedException();
        }

        selectable.setSelected(selectedState);
        if (selectedState) {
            selected.add(selectable);
        } else {
            selected.remove(selectable);
        }
        notifyDataSetChanged();
    }

    @Override
    public void loadUsers(List<UserDigest> userDigests) {
        String thisUserDigestId = userManager.getUser().getUserDigest().toHexString();
        List<UserDigest> users = new ArrayList<>(userDigests);

        Ln.d("ME: " + thisUserDigestId);
        for (UserDigest u : userDigests) {
            String id = u.getId().toHexString();
            Ln.d("IT: " + id);
            if (!thisUserDigestId.equals(id)){
                selectables.put(id, new Selectable(u));
            } else {
                users.remove(u);
            }
        }


        super.loadUsers(users);
    }

    public int getMaxSelection() {
        return maxSelection;
    }
}
