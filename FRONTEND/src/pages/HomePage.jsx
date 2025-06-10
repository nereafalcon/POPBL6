import Footer from '../components/HomePage/Footer.jsx';
import HeroHeader from '../components/HomePage/HeroHeader.jsx';
import PropertyList from '../components/HomePage/PropertyList.jsx';
import SearchBar from '../components/HomePage/SearchBar.jsx';

const HomePage = () => {
    return (
        <div className="min-h-screen flex flex-col">
            <HeroHeader />
            <SearchBar />
            <main className="container mx-auto px-4 sm:px-6 lg:px-8 py-18">
                <PropertyList />
            </main>
            <Footer />
        </div>
    );
}

export default HomePage;