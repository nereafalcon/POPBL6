import React from 'react';
import SubscribeBox from './SubscribeBox.jsx';
import RelatedSearches from './RelatedSearches.jsx';
import RecentlyViewed from './RecentlyViewed.jsx';

const Sidebar = () => {
    return (
        <aside className="lg:w-1/3 space-y-8">
            <SubscribeBox />
            <RelatedSearches />
            {/* <RecentlyViewed /> */}
        </aside>
    );
}

export default Sidebar;
