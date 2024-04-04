import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const NewClientPage = () => {
  const navigate = useNavigate();
  const apiHost = process.env.REACT_APP_CRUD_API_HOST;
  const [errorMessage, setErrorMessage] = useState('');
  const [newClient, setNewClient] = useState({
    name: '',
    passportNumber: '',
    email: ''
  });
  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewClient(prevClient => ({
      ...prevClient,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(apiHost + '/api/clients', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newClient)
      });
      if (response.ok) {
        const fetchedNewClient = await response.json();
        setErrorMessage(null);
        navigate(`/clients/${fetchedNewClient.id}`)
        setNewClient({
          name: '',
          passportNumber: '',
          email: ''
        });
      } else {
        const fetchedErrorResponse = await response.json();
        if (fetchedErrorResponse.info === "non unique value") {
          setErrorMessage(fetchedErrorResponse.details);
        } else if (fetchedErrorResponse.info === "validation errors") {
          setErrorMessage(fetchedErrorResponse.details.join(", "));
        }
      }
    } catch (error) {
      console.error('Ошибка при выполнении запроса:', error);
    }
  };

  return (
    <div>
      <label>
        Имя:
        <input
          type="text"
          name="name"
          value={newClient.name}
          onChange={handleChange}
        />
      </label>
      <label>
        Номер паспорта:
        <input
          type="text"
          name="passportNumber"
          value={newClient.passportNumber}
          onChange={handleChange}
        />
      </label>
      <label>
        Email:
        <input
          type="email"
          name="email"
          value={newClient.email}
          onChange={handleChange}
        />
      </label>
      <button onClick={handleSubmit}>Сохранить</button>
      {errorMessage && <div style={{ color: 'red' }}>{errorMessage}</div>}
    </div>
  );
};

export { NewClientPage as NewClientForm };

