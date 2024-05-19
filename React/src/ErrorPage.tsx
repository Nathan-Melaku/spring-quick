import { useRouteError } from 'react-router-dom';

function ErrorPage() {
  const error: any = useRouteError();
  console.error(error);

  return (
    <div id="error-page" className="m-auto flex flex-col items-center">
      <h1 className="text-primary text-6xl font-bold">Oops</h1>
      <p className="text-2xl text-accent mt-10">Sorry, an unexpected error has occured</p>
      <p className="text-neutral-content mt-10">
        <i>{error.statusText || error.message}</i>
      </p>
    </div>
  );
}

export default ErrorPage;
