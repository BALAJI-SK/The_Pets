package com.example.thepets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import  com.example.thepets.data.PetContract.PetEntry;
    /**
     * Displays list of pets that were entered and stored in the app.
     */
    public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final int PET_LOADER = 0;

        // This is the Adapter being used to display the list's pet
        PetCursorAdapter mCursorAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_catalog);

            // Setup FAB to open com.example.thepets.EditorActivity
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                    startActivity(intent);
                }
            });

            // Find the ListView which will be populated with the pet data
            ListView petListView = (ListView) findViewById(R.id.list);

            // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
            View emptyView = findViewById(R.id.empty_view);
            petListView.setEmptyView(emptyView);

            // Setup an adapter to create a list item for each row of pet data in the Cursor.
            // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
            mCursorAdapter = new PetCursorAdapter(this, null);
            petListView.setAdapter(mCursorAdapter);

            // Setup the item click listener
            petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    // Create new intent to go to {@link com.example.thepets.EditorActivity}
                    Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                    // From the content URI that represents the specific pet that was clicked on,
                    // by appending the "id" (passed as input to this method) onto the
                    // {@link PetEntry#CONTENT_URI}
                    // For example, the URI would be "content://com.example.android.pets/pets/2"
                    // if the pet with ID 2 was clicked on.
                    Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);

                    // Set the URI on the data field of the intent
                    intent.setData(currentPetUri);

                    // Launch the {@link com.example.thepets.EditorActivity} to display the data for the current pet.
                    startActivity(intent);
                }
            });

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(PET_LOADER, null, this);

        }

        /**
         * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
         */
        private void insertPet() {
            // Create a ContentValues object where column names are the keys,
            // and Toto's pet attributes are the values.
            ContentValues values = new ContentValues();
            values.put(PetEntry.COLUMN_PET_NAME, "Toto");
            values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
            values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
            values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

            // Insert a new row for Toto into the provider using the ContentResolver.
            // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
            // into the pets database table.
            // Receive the new content URI that will allow us to access Toto's data in the future.
            Uri newUri = getContentResolver().insert(
                    PetEntry.CONTENT_URI,   // the pet content URI
                    values                  // the values to insert
            );

        }

        /**
         * Helper method to delete all pets in the database.
         */
        private void deleteAllPets() {
            int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
            Log.v(CatalogActivity.class.getSimpleName(), rowsDeleted + " rows deleted from pet database");
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu options from the res/menu/menu_catalog.xml file.
            // This adds menu items to the app bar.
            getMenuInflater().inflate(R.menu.menu_catalog, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // User clicked on a menu option in the app bar overflow menu
            switch (item.getItemId()) {
                // Respond to a click on the "Insert dummy data" menu option
                case R.id.action_insert_dummy_data:
                    insertPet();
                    return true;
                // Respond to a click on the "Delete all entries" menu option
                case R.id.action_delete_all_entries:
                    deleteAllPets();
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        // Called when a new Loader needs to be created
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    PetEntry._ID,
                    PetEntry.COLUMN_PET_NAME,
                    PetEntry.COLUMN_PET_BREED
            };

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    this,
                    PetEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }

        // Called when a previously created loader has finished loading
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            // Swap the new cursor in.  (The framework will take care of closing the
            // old cursor once we return.)
            mCursorAdapter.swapCursor(cursor);
        }

        // Called when a previously created loader is reset, making the data unavailable
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // This is called when the last Cursor provided to onLoadFinished()
            // above is about to be closed.  We need to make sure we are no
            // longer using it.
            mCursorAdapter.swapCursor(null);
        }
    }
