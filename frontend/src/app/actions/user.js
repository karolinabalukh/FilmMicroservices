import config from 'config';
import {
  RECEIVE_USER,
  REQUEST_USER,
  REQUEST_SIGN_OUT,
} from '../constants/actionTypes';

const receiveUser = (user) => ({
  payload: user,
  type: RECEIVE_USER,
});

const requestUser = () => ({
  type: REQUEST_USER,
});

const requestSignOut = () => ({
  type: REQUEST_SIGN_OUT,
});

const fetchUser = () => async (dispatch) => {
  dispatch(requestUser());

  const { USERS_SERVICE } = config;

  try {
    const response = await fetch(`${USERS_SERVICE}/profile`, {
      method: 'GET',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    });

    if (response.ok) {
      const data = await response.json();
      console.log('Успішний вхід. Дані профілю:', data);

      const userForRedux = {
        id: data.sub || 'google-id',
        login: data.email,
        firstName: data.name || 'User',
        lastName: '',
        email: data.email,
        authorities: ['ENABLE_SEE_SECRET_PAGE']
      };

      dispatch(receiveUser(userForRedux));
    } else {
      console.log('Користувач не авторизований');
      dispatch(requestSignOut());
    }
  } catch (error) {
    console.error('Помилка при перевірці профілю:', error);
    dispatch(requestSignOut());
  }
};

const fetchSignOut = () => (dispatch) => {
  dispatch(requestSignOut());
  window.location.href = 'http://localhost:8081/logout';
};

const fetchSignIn = () => () => {};
const fetchSignUp = () => () => {};
const fetchRefreshToken = () => () => {};

const exportFunctions = {
  fetchRefreshToken,
  fetchSignIn,
  fetchSignOut,
  fetchSignUp,
  fetchUser,
};

export default exportFunctions;