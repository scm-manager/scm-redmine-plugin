package sonia.scm.redmine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.util.HttpUtil;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;

/**
*
* @author Marvin Froeder marvin_at_marvinformatics_dot_com
*/
public class RestRedmineHandlerFactory implements RedmineHandlerFactory
{

    /** the logger for SoapRedmineHandlerFactory */
    private static final Logger logger =
      LoggerFactory.getLogger(RestRedmineHandlerFactory.class);

    //~--- methods --------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param urlString
     * @param username
     * @param password
     *
     * @return
     *
     * @throws RemineConnectException
     */
    @Override
    public RedmineHandler createRedmineHandler(String urlString, String username,
            String password)
            throws RedmineException
    {
      RedmineHandler handler = null;

      try
      {
        //just validate if is a valid url
        HttpUtil.getUriWithoutEndSeperator(urlString);

        if (logger.isDebugEnabled())
        {
          logger.debug("connect to redmine {} as user {}", urlString, username);
        }

        RedmineManager mgr = new RedmineManager( urlString, username, password );

        handler = new RestRedmineHandler(mgr, username);
      }
      catch (NullPointerException ex)
      {
        throw new RedmineException(
            "could not connect to redmine instance at ".concat(urlString), ex);
      }

      return handler;
    }

}