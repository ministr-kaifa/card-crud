import React, { useEffect, useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { useNavigate, useParams } from 'react-router-dom';

const CardPage = () => {
  const { id } = useParams();
  const [card, setCard] = useState(null);
  const [editedCard, setEditedCard] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();
  const apiHost = process.env.REACT_APP_CRUD_API_HOST;

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(apiHost + `/api/cards/${id}`)
        const fetchedCard = await response.json();
        setCard(fetchedCard);
        setEditedCard({ ...fetchedCard });
      } catch (error) {
        console.error(error);
      }
    };

    fetchData();
  }, [id]);

  const handleFieldChange = (fieldName, value) => {
    setEditedCard({ ...editedCard, [fieldName]: value });
  };

  const handleDateChange = (fieldName, date) => {
    setEditedCard({ ...editedCard, [fieldName]: date.toISOString().substring(0, 10) });
  };

  const handleEditClick = () => {
    setIsEditing(true);
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    setEditedCard(card);
  };

  const handleSaveEdit = async () => {
    try {
      const response = await fetch(apiHost + `/api/cards/${id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(editedCard),
      });
      if (response.ok) {
        setErrorMessage(null);
        setIsEditing(false);
        setCard(editedCard);
      } else {
        const fetchedErrorResponse = await response.json();
        if (fetchedErrorResponse.info === "non unique value") {
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
      const response = await fetch(apiHost + `/api/cards/${id}`, {
        method: 'DELETE',
      });
      if (response.ok) {
        navigate('/clients');
      } else {
        console.error('Failed to delete card');
      }
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div>
      {card ? (
        <div>
          <p>ID: {card.id}</p>
          <p>Номер карты: {isEditing ? <input value={editedCard.cardNumber} onChange={(e) => handleFieldChange('cardNumber', e.target.value)} /> : card.cardNumber}</p>
          <p>Действительна с: {isEditing ? <DatePicker selected={editedCard.validFrom} onChange={(date) => handleDateChange('validFrom', date)} dateFormat="yyyy-MM-dd" /> : card.validFrom}</p>
          <p>Действительна до: {isEditing ? <DatePicker selected={editedCard.validTo} onChange={(date) => handleDateChange('validTo', date)} dateFormat="yyyy-MM-dd" /> : card.validTo}</p>
          {isEditing ? (
            <div>
              <button onClick={handleSaveEdit}>Сохранить</button>
              <button onClick={handleCancelEdit}>Отмена</button>
              {errorMessage && <div style={{ color: 'red' }}>{errorMessage}</div>}
            </div>
          ) : (
            <div>
              <button onClick={handleEditClick}>Редактировать</button>
              <button onClick={handleDelete}>Удалить</button>
            </div>
          )}
        </div>
      ) : (
        <p>Карта не найдена</p>
      )}
    </div>
  );
};

export { CardPage };

