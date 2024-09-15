import { useFormik } from "formik";
import { Link, useNavigate } from "react-router-dom";
import { api } from "./ApiConfig";
import axios from "axios";


const LoginPage = () => {

    // const dispatch = useDispatch();
    const navigate = useNavigate();
    const emailRegex = /^([a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z]{2,})$/;
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    const login = async(values) => {
      try {
        // event.preventDefault();
        const {data} = await axios.post('http://localhost:8081/auth/login', values);
        console.log('backend data for login call :', data);
        localStorage.setItem("jwt",data.accessToken);
        alert('login successful');
        navigate("/home");
      } catch (error) {
        console.log(error);
        alert('login failed');
      }

    }

    const formik = useFormik({
      initialValues : {
        email: '',
        password: ''
      },
      onSubmit: (values) => {
        console.log("form data: ", values)
        login(values);
      },
      validate: (values) => {
        const errors = {};
        if (!values.email || !emailRegex.test(values.email)) {
          errors.email = 'Required and must be a valid email address';
        }
        if (!values.password || !passwordRegex.test(values.password)) {
          errors.password = 'Required and must be 8 characters long, contain at least one uppercase letter, one lowercase letter, one number and one special';
        }
        return errors;
      }
    })

    return (
        <>
          <div className="flex min-h-full flex-1 flex-col justify-center px-6 py-12 lg:px-8">
            <div className="sm:mx-auto sm:w-full sm:max-w-sm">
              <h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
                Sign in to your account !
              </h2>
            </div>
    
            <div className="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
              <form onSubmit={formik.handleSubmit} autoComplete='off' className="space-y-6" >
                <div>
                  <label htmlFor="email" className="block text-sm font-medium leading-6 text-gray-900">
                    Email address
                  </label>
                  <div className="mt-2">
                    <input
                      id="email"
                      name="email"
                      type="email"
                      value={formik.values.email}
                      autoComplete="email"
                      // required
                      className="px-2 block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                    />
                    {formik.touched.email && formik.errors.email ? <p className="mt-1 text-center text-sm text-red-600">{formik.errors.email}</p> : null}
                  </div>
                </div>
    
                <div>
                  <div className="flex items-center justify-between">
                    <label htmlFor="password" className="block text-sm font-medium leading-6 text-gray-900">
                      Password
                    </label>
                    <div className="text-sm">
                      <Link to="/resetPassword" className="font-semibold text-indigo-600 hover:text-indigo-500">
                        Forgot password?
                      </Link>
                    </div>
                  </div>
                  <div className="mt-2">
                    <input
                      id="password"
                      name="password"
                      type="password"
                      value={formik.values.password}
                      autoComplete="current-password"
                      // required
                      className="px-2 block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                    />
                    {formik.touched.password && formik.errors.password ? <p className="mt-1 text-center text-sm text-red-600">{formik.errors.password}</p> : null}
                  </div>
                </div>
    
                <div>
                  <button 
                    type="submit"
                    className="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
                  >
                    Sign in
                  </button>
                </div>
              </form>
    
              <p className="mt-10 text-center text-sm text-gray-500">
                Not a member?{' '}
                <Link to="/register" onCl className="font-semibold leading-6 text-indigo-600 hover:text-indigo-500">
                  Register here
                </Link>
              </p>
            </div>
          </div>
        </>
      )}

export default LoginPage