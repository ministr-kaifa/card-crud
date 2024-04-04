import React from 'react';
import { Link, Route, Routes } from 'react-router-dom';

import { CardPage } from './pages/CardPage';
import { ClientPage } from './pages/ClientPage';
import { ClientsPage } from './pages/ClientsPage';
import { HomePage } from './pages/HomePage';
import { NewCardForm } from './pages/NewCardPage';
import { NewClientForm } from './pages/NewClientPage';

import './App.css';

function App() {

  const apiEventHost = process.env.REACT_APP_EVENT_MANAGER_API_HOST;

  const handleUpdateCards = async () => {
    try {
      await fetch(apiEventHost + `/api/events/generatorPublishers/cardExpiredEventGeneratorPublisher/tasks`,
        {
          method: 'POST'
        });
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <>
      <header>
        <Link to="/">Home</Link>
        <Link to="/clients">Клиенты</Link>
        <button onClick={handleUpdateCards}>Обновить карты</button>
      </header>
      <Routes>
        <Route path="/clients" element={<ClientsPage />} />
        <Route path="/clients/new" element={<NewClientForm />} />
        <Route path="/clients/:id" element={<ClientPage />} />
        <Route path="/cards/new" element={<NewCardForm />} />
        <Route path="/cards/:id" element={<CardPage />} />
        <Route path="/" element={<HomePage />} />
      </Routes>
    </>
  );
}

export default App;
