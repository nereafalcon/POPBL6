import React from "react";

const HeroHeader = () => (
    <header className="hero-gradient py-16 sm:pb-28 sm:pt-7">
        <div className="container mx-auto px-4 sm:px-6 lg:px-8">
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold text-gray-900 mb-6 leading-tight">
                <span className="block">Aurkitu zure ametsetako etxea gaur</span>
                <span className="block">minutu gutxi barru.</span>
            </h1>
            <p className="text-lg sm:text-xl text-gray-600 max-w-3xl">
                Ezagutu apartamentu paregabeak Euskal Herrian. Ikasle eta profesional gazteentzako iragazki pertsonalizatuak
                bizitzeko espazio aproposa aurkitzeko.
            </p>
        </div>
    </header>
);

export default HeroHeader;
