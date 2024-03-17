import React, { useState, useEffect } from 'react';
import { useParams, Routes, Route, Link, useNavigate } from 'react-router-dom';
import { CardPage } from './CardPage';
import { NewCardForm } from './NewCardPage';

const ClientPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [client, setClient] = useState(null);
  const [editedClient, setEditedClient] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const apiHost = process.env.REACT_APP_CRUD_API_HOST;
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(apiHost + `/api/clients/${id}`);
        const fetchedClient = await response.json();
        setClient(fetchedClient);
        setEditedClient({ ...fetchedClient });
      } catch (error) {
        console.error(error);
      }
    };

    fetchData();
  }, [id]);

  const handleFieldChange = (fieldName, value) => {
    setEditedClient({ ...editedClient, [fieldName]: value });
  };

  const handleEditClick = () => {
    setIsEditing(true);
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    setEditedClient(client);
  };

  const handleSaveEdit = async () => {
    try {
      const response = await fetch(apiHost + `/api/clients/${id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(editedClient),
      });
      if (response.ok) {
        setIsEditing(false);
        setClient(editedClient);
        setErrorMessage(null);
      } else {
        const fetchedErrorResponse = await response.json();
        if(fetchedErrorResponse.info === "non unique value") {
          setErrorMessage(fetchedErrorResponse.details);
        } else if (fetchedErrorResponse.info === "entity not found") {
          setErrorMessage(fetchedErrorResponse.details);
        } else if (fetchedErrorResponse.info === "validation errors") {
          setErrorMessage(fetchedErrorResponse.details.join(", "));
        }
      }
    } catch (error) {
      console.error(error);
    }
  };

  const handleDelete = async () => {
    try {
      const response = await fetch(apiHost + `/api/clients/${id}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        navigate('/clients');
      } else {
        console.error('Failed to delete client');
      }
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div>
      {client ? (
        <div>
          <p>ID: {client.id}</p>
          <p>Имя: {isEditing ? <input value={editedClient.name} onChange={(e) => handleFieldChange('name', e.target.value)} /> : client.name}</p>
          <p>Паспорт: {isEditing ? <input value={editedClient.passportNumber} onChange={(e) => handleFieldChange('passportNumber', e.target.value)} /> : client.passportNumber}</p>
          <p>Email: {isEditing ? <input value={editedClient.email} onChange={(e) => handleFieldChange('email', e.target.value)} /> : client.email}</p>
          <p>Карты:</p>
          <ul>
            {client.cards.map(card => (
              <li key={card.id}>
                <Link to={`/cards/${card.id}`}>{card.cardNumber}</Link>
              </li>
            ))}
          </ul>
          {isEditing ? (
            <div>
              <button onClick={handleSaveEdit}>Сохранить</button>
              <button onClick={handleCancelEdit}>Отмена</button>
              {errorMessage && <div style={{ color: 'red' }}>{errorMessage}</div>}
            </div>
          ) : (
            <div>
              <button onClick={() => navigate('/cards/new', { state: { ownerId: client.id } })}>Добавить карту</button>
              <button onClick={handleEditClick}>Редактировать</button>
              <button onClick={handleDelete}>Удалить</button> 
            </div>
          )}
        </div>
      ) : (
        <p>Пользователь не найден</p>
      )}
      <Routes>
        <Route path="/cards/new" element={<NewCardForm ownerId='' />} />
        <Route path="/cards/:cardId" element={<CardPage />} />
      </Routes>
    </div>
  );
};

export {ClientPage};
