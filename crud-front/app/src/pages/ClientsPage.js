import { React, useEffect, useState } from 'react';
import { Routes, Route, Link, useNavigate } from 'react-router-dom';
import { ClientPage } from './ClientPage';
import { NewClientForm } from './NewClientPage';

const ClientsPage = () => {
  const navigate = useNavigate();
  const [clients, setClients] = useState([]);
  const apiHost = process.env.REACT_APP_CRUD_API_HOST;

  useEffect(() => {
    const fetchData = async () => {
      try {
        console.log(apiHost + '/api/clients')
        const response = await fetch(apiHost + '/api/clients');
        const fetchedClients = await response.json();
        setClients(fetchedClients);
      } catch (error) {
        console.error(error);
      }
    };

    fetchData();
  }, []);

  return (
    <div>
      <ul>
        {clients.map((client) => (
          <li key={client.id}>
            <Link to={`${client.id}`}>{client.name}</Link>
          </li>
        ))}
      </ul>
      <button onClick={() => navigate('/clients/new')}>Создать клиента</button>
      <Routes>
        <Route path='new' element={<NewClientForm />} />
        <Route path=':id' element={<ClientPage />} />
      </Routes>
    </div>
  );
};

export { ClientsPage };
