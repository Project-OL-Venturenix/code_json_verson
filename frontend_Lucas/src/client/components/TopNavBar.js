import React, {useContext} from 'react';
import { Navbar} from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleUser } from '@fortawesome/free-solid-svg-icons';
import {LoginUserContext} from './App';

export default function TopNavBar() {
    const loginUser = useContext(LoginUserContext);

    return (
        <>
            <Navbar
                sticky="top"
                style={{
                    backgroundColor: '#8AC453',
                    width: '100vw',
                    height: '80px',
                    display: 'flex',
                    flexDirection: 'row',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                }}
            >
                <div>

                    <div />

                    <div style={{ display: 'flex', alignItems: 'center' }}>
                        <FontAwesomeIcon
                            icon={faCircleUser}
                            style={{
                                color: '#FFD43B',
                                marginRight: '10px',
                            }}
                            size="4x"
                        />
                        <span style={{ fontSize: '2em' }}>{loginUser.name}</span>
                    </div>

                    <div/>

                </div>
            </Navbar>
        </>
    );
}
